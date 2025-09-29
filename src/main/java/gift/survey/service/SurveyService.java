package gift.survey.service;

import gift.survey.domain.SurveyResponse;
import gift.survey.dto.SurveyResponseResult;
import gift.survey.repository.SurveyResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyResponseRepository surveyResponseRepository;

    public SurveyResponseResult saveSurveyResponse(SurveyResponse request) {
        // 응답 시간 기록
        request.setSubmittedAt(LocalDateTime.now());

        // DB 저장
        surveyResponseRepository.save(request);

        return SurveyResponseResult.builder()
                .status("success")
                .message("설문 응답이 정상적으로 저장되었습니다.")
                .responseId(UUID.randomUUID().toString())
                .submittedAt(request.getSubmittedAt().toString())
                .build();
    }
}
