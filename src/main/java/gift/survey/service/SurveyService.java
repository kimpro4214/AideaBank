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

    /** 기존: DB 응답 저장 */
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

    /** 신규: 최초 방문 시 CSV 기본값 생성 */
    public void createUserRow(String userId) {
        CsvUtil.addUser(userId);  // 내부에서 non-start로 기본값 생성
    }

    /** 설문 시작 → 상태 = in-progress */
    public void startSurvey(String userId, String type) {
        CsvUtil.updateStatus(userId, type, "in-progress");
    }

    /** 설문 완료 → 상태 = completed */
    public void completeSurvey(String userId, String type) {
        String current = CsvUtil.getStatus(userId, type);

        if (current.equals("non-start")) {
            throw new IllegalStateException("설문을 시작하지 않았습니다.");
        }

        CsvUtil.updateStatus(userId, type, "completed");
    }

    /** 점수 저장 */
    public void saveGdsScore(String userId, int score) {
        CsvUtil.updateScore(userId, "gds_score", score);
    }

    public void saveMmseScore(String userId, String part, int score) {
        CsvUtil.updateScore(userId, part + "_score", score);
    }

    /** 상태 조회 */
    public Map<String, Object> getSurveyStatus(String userId) {
        String[] s = CsvUtil.getStatusRow(userId);

        return Map.of(
                "basic_status", s[1],
                "mmse_status", s[2],
                "gds_status", s[3],
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
