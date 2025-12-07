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

    @Value("${ai.server.url:https://forgeable-cryoscopic-theresia.ngrok-free.dev/diagnose}")
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

        // 1) 설문 데이터 읽기
        Map<String, Object> status = surveyService.getSurveyStatus(userId);

        Map<String, Integer> mmse = (Map<String, Integer>) status.get("mmse_scores");
        Map<String, Object> basic = (Map<String, Object>) status.get("basic_survey");

        if (mmse == null || basic == null) {
            return ResponseEntity.badRequest().body("설문 데이터가 부족합니다.");
        }

        // 2) CSV 파일 생성
        File mmseCsv = createMmseCsv(userId, mmse);
        File basicCsv = createBasicCsv(userId, basic);

        // 3) AI 서버 호출 (RAG + LLM 통합 모델)
        Map<String, Object> aiResult = sendToAiServer(mmseCsv, basicCsv);

        // 4) 최종 결과 그대로 반환
        return ResponseEntity.ok(aiResult);
    }

    /** MMSE CSV 생성 */
    private File createMmseCsv(String userId, Map<String, Integer> mmseScores) {
        try {
            File file = new File("/home/ubuntu/tmp/mmse_" + userId + ".csv");
            file.getParentFile().mkdirs();

            FileWriter writer = new FileWriter(file);
            writer.write("question,score\n");

            for (int i = 1; i <= 12; i++) {
                writer.write("mmse-" + i + "," + mmseScores.get("mmse-" + i) + "\n");
            }

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
        body.add("mmse", new FileSystemResource(mmseCsv));
        body.add("basic", new FileSystemResource(basicCsv));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                rest.exchange(aiServerUrl, HttpMethod.POST, request, Map.class);

        return response.getBody();
    }
}
