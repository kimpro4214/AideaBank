package gift.survey.controller;

import gift.survey.domain.SurveyResponse;
import gift.survey.dto.SurveyResponseResult;
import gift.survey.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/surveys")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    // 설문 응답 저장
    @PostMapping("/responses")
    public SurveyResponseResult submitSurvey(@RequestBody SurveyResponse request) {
        return surveyService.saveSurveyResponse(request);
    }
}
