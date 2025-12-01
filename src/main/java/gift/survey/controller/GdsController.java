package gift.survey.controller;

import gift.survey.dto.GdsResponse;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/gds")
public class GdsController {

    /**
     * 1) GDS 점수 계산 API
     */
    @PostMapping("/score")
    public ResponseEntity<GdsResponse> calculate(@RequestBody Map<String, Integer> answers) {

        int score = answers.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        String level;
        if (score < 10) level = "가벼운 우울";
        else if (score < 17) level = "중등도 우울";
        else level = "심한 우울";

        boolean depressionRisk = score >= 17;

        GdsResponse response = new GdsResponse(score, level, depressionRisk);
        return ResponseEntity.ok(response);
    }


    /**
     * 2) GDS 점수 계산 후 AI 서버로 전송하는 API
     */
    @PostMapping("/predict")
    public ResponseEntity<?> predictToAi(@RequestBody Map<String, Integer> answers) {

        int score = answers.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        // 실제 AI 서버가 없으므로 현재는 mock 응답 리턴
        Map<String, Object> mock = new HashMap<>();
        mock.put("gds_score", score);
        mock.put("ai_result", "AI 서버 없음 - Stub 응답");
        mock.put("risk_level", score >= 17 ? "High" : "Low");

        return ResponseEntity.ok(mock);
    }

//    @PostMapping("/predict")
//    public ResponseEntity<?> predictToAi(@RequestBody Map<String, Integer> answers) {
//
//        // 1. 점수 계산
//        int score = answers.values().stream()
//                .mapToInt(Integer::intValue)
//                .sum();
//
//        // 2. AI 서버로 전달할 Body 생성
//        Map<String, Object> aiRequestBody = new HashMap<>();
//        aiRequestBody.put("gds_score", score);
//
//        // 3. AI 서버 URL
//        String aiUrl = "http://your-ai-server/predict"; // ← 실제 AI 서버 주소로 교체
//
//        // 4. RestTemplate 준비
//        RestTemplate restTemplate = new RestTemplate();
//
//        // 5. 요청 헤더 설정 (JSON)
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<Map<String, Object>> requestEntity =
//                new HttpEntity<>(aiRequestBody, headers);
//
//        // 6. AI 서버 호출
//        ResponseEntity<String> aiResponse =
//                restTemplate.exchange(aiUrl, HttpMethod.POST, requestEntity, String.class);
//
//        // 7. AI 서버 응답 그대로 반환
//        return ResponseEntity.ok(aiResponse.getBody());
//    }
}
