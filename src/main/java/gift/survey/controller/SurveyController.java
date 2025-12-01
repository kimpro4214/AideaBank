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

    /** 기존 기능: 설문 응답 저장 (POST 유지 — 본문 필요) */
    @PostMapping("/responses")
    public SurveyResponseResult submitSurvey(@RequestBody SurveyResponse request) {
        return surveyService.saveSurveyResponse(request);
    }

    /** 최초 방문 → user_id 쿠키 발급 & CSV row 생성 */
    @GetMapping("/init")
    public ResponseEntity<?> initUser(HttpServletResponse response) {

        String uuid = UUID.randomUUID().toString();

        ResponseCookie cookie = ResponseCookie.from("user_id", uuid)
                .path("/")
                .httpOnly(false)
                .maxAge(60L * 60 * 24 * 30)   // 30일
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        surveyService.createUserRow(uuid);

        return ResponseEntity.ok("user initialized");
    }

    /** 설문 시작 (GET 버전) */
    @GetMapping("/start")
    public ResponseEntity<?> startSurvey(
            @CookieValue("user_id") String userId,
            @RequestParam("type") String type
    ) {
        surveyService.updateSurveyStatus(userId, type, false);
        return ResponseEntity.ok()
                .header("Cache-Control", "no-store")
                .body("started");
    }

    /** 설문 완료 (GET 버전) */
    @GetMapping("/complete")
    public ResponseEntity<?> completeSurvey(
            @CookieValue("user_id") String userId,
            @RequestParam("type") String type
    ) {
        surveyService.updateSurveyStatus(userId, type, true);
        return ResponseEntity.ok()
                .header("Cache-Control", "no-store")
                .body("completed");
    }

    /** 설문 상태 조회 */
    @GetMapping("/status")
    public ResponseEntity<?> getStatus(
            @CookieValue("user_id") String userId
    ) {
        Map<String, Object> result = surveyService.getSurveyStatus(userId);
        return ResponseEntity.ok(result);
    }
}
