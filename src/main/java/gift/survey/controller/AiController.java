package gift.survey.controller;

import gift.survey.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import org.springframework.core.io.FileSystemResource;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final SurveyService surveyService;

    @Value("${ai.server.url:https://victor-consolatory-reasonlessly.ngrok-free.dev/diagnose-from-files}")
    private String aiServerUrl;

    /**
     * 📌 전체 통합 진단 API
     * 1) CSV 생성
     * 2) AI 서버로 CSV 업로드
     * 3) AI 서버에서 RAG + LLM + Final Report 모두 수행
     * 4) 최종 JSON 그대로 프론트로 반환
     */
    @PostMapping("/diagnose")
    public ResponseEntity<?> diagnose(@CookieValue("user_id") String userId) {

        Map<String, Object> status = surveyService.getSurveyStatus(userId);

        Map<String, Integer> mmse = (Map<String, Integer>) status.get("mmse_scores");
        Map<String, Object> basic = (Map<String, Object>) status.get("basic_survey");

        if (mmse == null || basic == null) {
            return ResponseEntity.badRequest().body("설문 데이터가 부족합니다.");
        }

        File mmseCsv = createMmseCsv(userId, mmse);
        File basicCsv = createBasicCsv(userId, basic);

        Map<String, Object> aiResult = sendToAiServer(mmseCsv, basicCsv);

        // 🔥 AI 서버가 준 프롬프트 꺼내기
        String prompt = (String) aiResult.get("prompt");

        // 🔥 프롬프트를 Gemini에 전달해 소견서 생성
        String llmReport = callGemini(prompt);

        // 🔥 전체 결과를 조합해 프론트로 전달
        return ResponseEntity.ok(Map.of(
                "model_result", aiResult,   // 통합 모델 결과 + RAG 결과
                "llm_report", llmReport     // 최종 의학적 소견 (Gemini)
        ));
    }


    /** MMSE CSV 생성 */
    private File createMmseCsv(String userId, Map<String, Integer> mmseScores) {
        try {
            File file = new File("/home/ubuntu/tmp/mmse_" + userId + ".csv");
            file.getParentFile().mkdirs();

            FileWriter writer = new FileWriter(file);

            // 1행: mmse-1, mmse-2, ..., mmse-12
            writer.write("mmse-1,mmse-2,mmse-3,mmse-4,mmse-5,mmse-6,mmse-7,mmse-8,mmse-9,mmse-10,mmse-11,mmse-12\n");

            // 2행: 점수들
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
                            mmseScores.get("mmse-12")
                            + "\n"
            );

            writer.close();
            return file;

        } catch (Exception e) {
            throw new RuntimeException("MMSE CSV 생성 실패", e);
        }
    }


    /** BASIC CSV 생성 */
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
            throw new RuntimeException("BASIC CSV 생성 실패", e);
        }
    }

    /**
     * 📌 AI 통합 서버로 CSV 업로드
     * Response 예시:
     * {
     *   "diagnosis": "CN",
     *   "probability": { "CN": 0.72, "MCI": 0.28, "AD": 0.0 },
     *   "report": "이 환자는..."
     * }
     */
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

        return response.getBody();
    }

    @Value("${GEMINI_API_KEY}")
    private String geminiApiKey;

    private String callGemini(String prompt) {
        try {
            RestTemplate rest = new RestTemplate();

            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateText?key="
                    + geminiApiKey;

            Map<String, Object> requestBody = Map.of(
                    "prompt", Map.of("text", prompt),
                    "max_output_tokens", 500
            );

            ResponseEntity<Map> response =
                    rest.postForEntity(url, requestBody, Map.class);

            Map result = response.getBody();
            if (result == null) return "Gemini 응답 없음";

            var candidates = (java.util.List<Map>) result.get("candidates");
            var output = (Map<String, Object>) candidates.get(0).get("output");

            return (String) output.get("text");

        } catch (Exception e) {
            return "Gemini 호출 오류: " + e.getMessage();
        }
    }
}
