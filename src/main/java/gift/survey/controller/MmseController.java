package gift.survey.controller;

import gift.survey.dto.*;
import gift.survey.service.MmseService;
import gift.survey.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mmse")
@RequiredArgsConstructor
public class MmseController {

    private final MmseService mmseService;
    private final SurveyService surveyService;

    /**
     * MMSE 문제 불러오기
     */
    @GetMapping("/questions")
    public ResponseEntity<MmseQuestionResponse> getQuestions() {
        return ResponseEntity.ok(mmseService.getQuestions());
    }


    /**
     * MMSE 제출 (기존 기능)
     * DB 저장 + 파일 업로드 기능 그대로 유지
     */
    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MmseSubmitResponse> submit(
            @RequestParam Long userId,
            @RequestPart("answers") String answersJson,
            @RequestPart(required = false) List<MultipartFile> files
    ) {
        return ResponseEntity.ok(mmseService.saveResponse(userId, answersJson, files));
    }


    /**
     * MMSE 결과 조회 (기존 기능)
     */
    @GetMapping("/result/{userId}")
    public ResponseEntity<MmseResultResponse> getResult(@PathVariable Long userId) {
        return ResponseEntity.ok(mmseService.getResult(userId));
    }


    /**
     * 🆕 MMSE 점수 저장 (CSV)
     */
    @PostMapping("/score")
    public ResponseEntity<?> saveMmseScore(
            @CookieValue("user_id") String anonymousUserId,  // CSV용 userId
            @RequestParam("part") String part,               // 예: mmse_time
            @RequestBody Map<String, Integer> body
    ) {
        int score = body.get("score");

        // CSV에 점수 저장
        surveyService.saveMmseScore(anonymousUserId, part, score);

        // MMSE 전체 완료 처리
        surveyService.updateSurveyStatus(anonymousUserId, "mmse", true);

        return ResponseEntity.ok(Map.of(
                "message", "MMSE score saved",
                "part", part,
                "score", score
        ));
    }
}
