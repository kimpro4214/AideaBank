package gift.survey.controller;

import gift.survey.domain.SurveyResponse;
import gift.survey.dto.SurveyResponseResult;
import gift.survey.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/surveys")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    /** DB 저장 */
    @PostMapping("/responses")
    public SurveyResponseResult submitSurvey(@RequestBody SurveyResponse request) {
        return surveyService.saveSurveyResponse(request);
    }

    /** 최초 접속 → user_id 발급 + CSV row 생성 */
    @GetMapping("/init")
    public ResponseEntity<?> initUser(HttpServletResponse response) {

        String uuid = UUID.randomUUID().toString();

        ResponseCookie cookie = ResponseCookie.from("user_id", uuid)
                .path("/")
                .httpOnly(false)
                .maxAge(60L * 60 * 24 * 30)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        surveyService.createUserRow(uuid);

        return ResponseEntity.ok("user initialized");
    }

    /** 설문 시작 */
    @GetMapping("/start")
    public ResponseEntity<?> startSurvey(
            @CookieValue("user_id") String userId,
            @RequestParam("type") String type
    ) {
        surveyService.startSurvey(userId, type);
        return ResponseEntity.ok("started");
    }

    /** 설문 완료 */
    @GetMapping("/complete")
    public ResponseEntity<?> completeSurvey(
            @CookieValue("user_id") String userId,
            @RequestParam("type") String type
    ) {
        try {
            surveyService.completeSurvey(userId, type);
            return ResponseEntity.ok("completed");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /** 상태 조회 (MMSE + GDS + BASIC 설문 값 전부 포함) */
    @GetMapping("/status")
    public ResponseEntity<?> getStatus(
            @CookieValue("user_id") String userId
    ) {
        return ResponseEntity.ok(surveyService.getSurveyStatus(userId));
    }

    /** 기본 설문 저장 */
    @PostMapping("/basic")
    public ResponseEntity<?> saveBasicSurvey(
            @CookieValue("user_id") String userId,
            @RequestBody Map<String, Integer> body
    ) {
        surveyService.saveBasicSurvey(
                userId,
                body.get("age_cognition"),
                body.get("sex"),
                body.get("race"),
                body.get("education")
        );

        return ResponseEntity.ok(Map.of("status", "saved"));
    }

    /** 기본 설문 조회 */
    @GetMapping("/basic")
    public ResponseEntity<?> getBasicSurvey(
            @CookieValue("user_id") String userId
    ) {
        return ResponseEntity.ok(surveyService.getBasicSurvey(userId));
    }
}
