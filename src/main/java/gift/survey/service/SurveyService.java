package gift.survey.service;

import gift.survey.domain.SurveyResponse;
import gift.survey.dto.SurveyResponseResult;
import gift.survey.repository.SurveyResponseRepository;
import gift.survey.util.CsvUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyResponseRepository surveyResponseRepository;

    // 기존: DB에 설문 응답 저장
    public SurveyResponseResult saveSurveyResponse(SurveyResponse request) {
        request.setSubmittedAt(LocalDateTime.now());
        surveyResponseRepository.save(request);

        return SurveyResponseResult.builder()
                .status("success")
                .message("설문 응답이 정상적으로 저장되었습니다.")
                .responseId(UUID.randomUUID().toString())
                .submittedAt(request.getSubmittedAt().toString())
                .build();
    }

    // 신규: 최초 접속 시 CSV row 생성
    public void createUserRow(String userId) {
        CsvUtil.addUser(userId);
    }

    // 완료 여부 업데이트 (basic, mmse, gds)
    public void updateSurveyStatus(String userId, String type, boolean completed) {
        CsvUtil.updateStatus(userId, type, completed);
    }

    // GDS 점수 저장
    public void saveGdsScore(String userId, int score) {
        CsvUtil.updateScore(userId, "gds_score", score);
    }

    // MMSE 점수 저장
    public void saveMmseScore(String userId, String part, int score) {
        CsvUtil.updateScore(userId, part + "_score", score);
    }

    // 상태 조회 (점수 포함)
    public Map<String, Object> getSurveyStatus(String userId) {
        String[] s = CsvUtil.getStatus(userId);

        return Map.of(
                "basic_completed", Boolean.parseBoolean(s[1]),
                "mmse_completed", Boolean.parseBoolean(s[2]),
                "gds_completed", Boolean.parseBoolean(s[3]),
                "gds_score", Integer.parseInt(s[4]),
                "mmse_time_score", Integer.parseInt(s[5]),
                "mmse_registration_score", Integer.parseInt(s[6]),
                "mmse_recall_score", Integer.parseInt(s[7]),
                "mmse_attention_score", Integer.parseInt(s[8]),
                "mmse_language_score", Integer.parseInt(s[9]),
                "mmse_copy_score", Integer.parseInt(s[10])
        );
    }
}
