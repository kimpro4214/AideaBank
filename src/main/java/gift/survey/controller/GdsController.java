package gift.survey.controller;

import gift.survey.dto.GdsResponse;
import gift.survey.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/gds")
@RequiredArgsConstructor
public class GdsController {

    private final SurveyService surveyService;

    /**
     * 1) GDS 점수 계산 + CSV에 저장 + 상태 완료 처리
     */
    @PostMapping("/score")
    public ResponseEntity<GdsResponse> calculate(
            @CookieValue("user_id") String userId,
            @RequestBody Map<String, Integer> answers
    ) {

        // 점수 계산
        int score = answers.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        // CSV에 점수 저장
        surveyService.saveGdsScore(userId, score);

        // GDS 완료 상태 true 로 저장
        surveyService.updateSurveyStatus(userId, "gds", true);

        // level 계산
        String level;
        if (score < 10) level = "가벼운 우울";
        else if (score < 17) level = "중등도 우울";
        else level = "심한 우울";

        boolean depressionRisk = score >= 17;

        GdsResponse response = new GdsResponse(score, level, depressionRisk);
        return ResponseEntity.ok(response);
    }


    /**
     * 2) GDS 점수를 AI 서버로 전송하는 API (현재는 stub)
     */
    @PostMapping("/predict")
    public ResponseEntity<?> predictToAi(
            @CookieValue("user_id") String userId,
            @RequestBody Map<String, Integer> answers
    ) {

        int score = answers.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        // CSV 저장 (점수 저장 + 완료 표시)
        surveyService.saveGdsScore(userId, score);
        surveyService.updateSurveyStatus(userId, "gds", true);

        // stub 응답
        Map<String, Object> mock = new HashMap<>();
        mock.put("gds_score", score);
        mock.put("ai_result", "AI 서버 없음 - Stub 응답");
        mock.put("risk_level", score >= 17 ? "High" : "Low");

        return ResponseEntity.ok(mock);
    }
}
