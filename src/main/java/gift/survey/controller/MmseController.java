package gift.survey.controller;

import gift.survey.dto.*;
import gift.survey.service.MmseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/mmse")
@RequiredArgsConstructor
public class MmseController {

    private final MmseService mmseService;

    @GetMapping("/questions")
    public ResponseEntity<MmseQuestionResponse> getQuestions() {
        return ResponseEntity.ok(mmseService.getQuestions());
    }

    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MmseSubmitResponse> submit(
            @RequestParam Long userId,
            @RequestPart("answers") String answersJson,
            @RequestPart(required = false) List<MultipartFile> files
    ) {
        return ResponseEntity.ok(mmseService.saveResponse(userId, answersJson, files));
    }

    @GetMapping("/result/{userId}")
    public ResponseEntity<MmseResultResponse> getResult(@PathVariable Long userId) {
        return ResponseEntity.ok(mmseService.getResult(userId));
    }
}
