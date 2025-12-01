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
    /**
     * 🆕 MMSE 문항별 점수 저장 (CSV)
     * 프론트에서 mmse-1 ~ mmse-12 점수 + totalScore 를 보내면
     * 날짜별 CSV 파일에 저장
     */
    @PostMapping("/score")
    public ResponseEntity<?> saveMmseRawScore(
            @CookieValue("user_id") String userId,               // UUID 기반 익명 사용자 ID
            @RequestBody MmseRawScoreRequest request            // 문항별 점수 + totalScore
    ) {
        mmseService.saveRawMmse(userId, request);
        mmseService.updateMmseStatusScores(userId, request);

        return ResponseEntity.ok(Map.of(
                "status", "OK",
                "message", "MMSE raw+converted scores stored",
                "userId", userId
        ));
    }

}
