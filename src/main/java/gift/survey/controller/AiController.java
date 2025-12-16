package gift.survey.controller;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import gift.survey.service.SurveyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class AiController {

    private final SurveyService surveyService;
    private final Client genAIClient;           // Gemini SDK Client
    private final String geminiModel = "gemini-2.5-flash";

    @Value("${ai.server.url:https://victor-consolatory-reasonlessly.ngrok-free.dev/diagnose-from-files}")
    private String aiServerUrl;

    @Value("${GEMINI_API_KEY}")
    private String geminiApiKey; // 지금 구조에선 안 써도 되지만 남겨둠

    /**
     * 📌 전체 통합 진단 API
     */
    @PostMapping("/diagnose")
    public ResponseEntity<?> diagnose(@CookieValue("user_id") String userId) {

        // 1) 설문 상태 조회
        Map<String, Object> status = surveyService.getSurveyStatus(userId);
        if (status == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("설문 상태를 조회할 수 없습니다. 먼저 설문을 진행해 주세요.");
        }

        @SuppressWarnings("unchecked")
        Map<String, Integer> mmse = (Map<String, Integer>) status.get("mmse_scores");
        @SuppressWarnings("unchecked")
        Map<String, Object> basic = (Map<String, Object>) status.get("basic_survey");

        if (mmse == null || mmse.isEmpty() || basic == null || basic.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("설문 데이터가 부족합니다. 기본 설문과 MMSE 설문을 모두 완료해 주세요.");
        }

        try {
            // 2) CSV 생성
            File mmseCsv = createMmseCsv(userId, mmse);
            File basicCsv = createBasicCsv(userId, basic);

            // 3) AI 서버 호출
            Map<String, Object> aiResult = sendToAiServer(mmseCsv, basicCsv);
            log.info("AI 서버 응답: {}", aiResult);

            if (aiResult == null || aiResult.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("AI 서버 응답이 비어 있습니다.");
            }

            Object promptObj = aiResult.get("llm_prompt");
            String prompt = (promptObj != null) ? String.valueOf(promptObj) : null;

            if (prompt == null || prompt.isBlank()) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of(
                                "message", "AI 서버에서 llm_prompt를 반환하지 않았습니다.",
                                "model_result", aiResult
                        ));
            }

            // ✅ 3.5) 프롬프트에 '오늘 작성일' 지시문 추가 + 프롬프트 내 모든 날짜를 오늘로 치환
            String patchedPrompt = preparePromptForToday(prompt);

            // 4) Gemini 호출하여 소견서 생성
            String llmReport = callGemini(patchedPrompt);

            return ResponseEntity.ok(Map.of(
                    "model_result", aiResult,
                    "llm_report", llmReport
            ));

        } catch (Exception e) {
            log.error("통합 진단 처리 중 예외 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("통합 진단 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /** ---------------- 프롬프트: 오늘 날짜 강제 ---------------- **/
    private String preparePromptForToday(String prompt) {
        if (prompt == null) return null;

        ZoneId kst = ZoneId.of("Asia/Seoul");
        LocalDate today = LocalDate.now(kst);

        // 예: 2025년 12월 16일
        String todayKo = today.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일", Locale.KOREAN));
        // 예: 2025-12-16
        String todayIso = today.format(DateTimeFormatter.ISO_LOCAL_DATE);

        // (1) 프롬프트 맨 위에 지시문 1줄(몇 줄) 추가
        String prefix =
                "### 작성일: " + todayKo + "\n" +
                        "- 아래 내용을 바탕으로 소견서를 작성하되, 날짜 표기는 반드시 작성일(오늘) 기준으로 작성하세요.\n" +
                        "- 입력에 과거 날짜가 포함되어 있더라도, 소견서에는 작성일(오늘) 기준으로 표기하세요.\n\n";

        // 중복 방지(이미 넣었으면 또 안 넣음)
        if (!prompt.startsWith("### 작성일:")) {
            prompt = prefix + prompt;
        }

        // (2) 프롬프트 내부에 있는 날짜 표현을 "전부" 오늘로 치환
        // 2-1) "23년 10월 27일", "2023년10월27일" 같은 한글 날짜
        String koreanDatePattern = "(?<!\\d)(?:\\d{2}|\\d{4})\\s*년\\s*\\d{1,2}\\s*월\\s*\\d{1,2}\\s*일(?!\\d)";
        prompt = prompt.replaceAll(koreanDatePattern, todayKo);

        // 2-2) "2023-10-27", "2023.10.27", "2023/10/27", "23-10-27" 같은 구분자 날짜
        String numericDatePattern = "(?<!\\d)(?:\\d{2}|\\d{4})[-./]\\d{1,2}[-./]\\d{1,2}(?!\\d)";
        prompt = prompt.replaceAll(numericDatePattern, todayIso);

        return prompt;
    }

    /** ---------------- CSV 생성 (MMSE) ---------------- **/
    private File createMmseCsv(String userId, Map<String, Integer> mmseScores) {
        try {
            File file = new File("/home/ubuntu/tmp/mmse_" + userId + ".csv");
            file.getParentFile().mkdirs();

            FileWriter writer = new FileWriter(file);

            writer.write("mmse-1,mmse-2,mmse-3,mmse-4,mmse-5,mmse-6,mmse-7,mmse-8,mmse-9,mmse-10,mmse-11,mmse-12\n");

            writer.write(
                    mmseScores.get("mmse-1") + "," +
                            mmseScores.get("mmse-2") + "," +
                            mmseScores.get("mmse-3") + "," +
                            mmseScores.get("mmse-4") + "," +
                            mmseScores.get("mmse-5") + "," +
                            mmseScores.get("mmse-6") + "," +
                            mmseScores.get("mmse-7") + "," +
                            mmseScores.get("mmse-8") + "," +
                            mmseScores.get("mmse-9") + "," +
                            mmseScores.get("mmse-10") + "," +
                            mmseScores.get("mmse-11") + "," +
                            mmseScores.get("mmse-12") + "\n"
            );

            writer.close();
            return file;
        } catch (Exception e) {
            log.error("MMSE CSV 생성 실패", e);
            throw new RuntimeException("MMSE CSV 생성 실패", e);
        }
    }

    /** ---------------- CSV 생성 (BASIC) ---------------- **/
    private File createBasicCsv(String userId, Map<String, Object> basic) {
        try {
            File file = new File("/home/ubuntu/tmp/basic_" + userId + ".csv");
            file.getParentFile().mkdirs();

            FileWriter writer = new FileWriter(file);
            writer.write("PHC_Age_Cognition,PHC_Sex,PHC_Race,PHC_Education\n");

            writer.write(
                    basic.get("age_cognition") + "," +
                            basic.get("sex") + "," +
                            basic.get("race") + "," +
                            basic.get("education") + "\n"
            );

            writer.close();
            return file;
        } catch (Exception e) {
            log.error("BASIC CSV 생성 실패", e);
            throw new RuntimeException("BASIC CSV 생성 실패", e);
        }
    }

    /** ---------------- AI 서버 CSV 업로드 ---------------- **/
    private Map<String, Object> sendToAiServer(File mmseCsv, File basicCsv) {

        RestTemplate rest = new RestTemplate();

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("mmse_file", new FileSystemResource(mmseCsv));
        body.add("basic_file", new FileSystemResource(basicCsv));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                rest.exchange(aiServerUrl, HttpMethod.POST, request, Map.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> result = response.getBody();
        return result;
    }

    /** ---------------- Gemini 텍스트 호출 (fromText 안 씀) ---------------- **/
    private String callGemini(String prompt) {
        try {
            // 프롬프트가 길면 로그가 너무 커질 수 있어서 길이만 찍고 싶으면 아래로 바꿔도 됨
            // log.info("Gemini 호출 프롬프트 길이: {}", (prompt != null ? prompt.length() : 0));
            log.info("Gemini 호출 프롬프트: {}", prompt);

            if (prompt == null || prompt.isBlank()) {
                return "Gemini 호출 실패: 프롬프트가 비어 있습니다.";
            }

            GenerateContentConfig config = GenerateContentConfig.builder()
                    .responseMimeType("text/plain")
                    .build();

            GenerateContentResponse response =
                    genAIClient.models.generateContent(geminiModel, prompt, config);

            if (response == null) {
                log.warn("Gemini 응답이 null입니다.");
                return "Gemini 응답이 null입니다.";
            }

            String text = response.text();
            log.info("Gemini 응답 text: {}", text);

            if (text == null || text.isBlank()) {
                return "Gemini가 빈 응답을 반환했습니다.";
            }

            return text.trim();

        } catch (Exception e) {
            log.error("Gemini 호출 중 오류 발생", e);
            return "Gemini 호출 오류: " + e.getMessage();
        }
    }
}
