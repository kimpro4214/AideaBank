package gift.survey.service;

import gift.survey.domain.SurveyResponse;
import gift.survey.dto.SurveyResponseResult;
import gift.survey.repository.SurveyResponseRepository;
import gift.survey.util.CsvUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyResponseRepository surveyResponseRepository;

    /** DB 저장 */
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

    /** 최초 방문 시 CSV row 생성 */
    public void createUserRow(String userId) {
        CsvUtil.addUser(userId);
    }

    /** 설문 시작 */
    public void startSurvey(String userId, String type) {
        CsvUtil.updateStatus(userId, type, "in-progress");
    }

    /** 설문 완료 */
    public void completeSurvey(String userId, String type) {
        String current = CsvUtil.getStatus(userId, type);

        if (current.equals("non-start")) {
            throw new IllegalStateException("설문을 시작하지 않았습니다.");
        }

        CsvUtil.updateStatus(userId, type, "completed");
    }

    /** GDS 점수 저장 */
    public void saveGdsScore(String userId, int score) {
        CsvUtil.updateScore(userId, "gds_score", score);
    }

    /** BASIC 설문 저장 */
    public void saveBasicSurvey(
            String userId,
            int ageCognition,
            int sex,
            int race,
            int education
    ) {
        CsvUtil.updateBasicSurvey(userId, ageCognition, sex, race, education);
    }

    /** BASIC 설문 조회 */
    public Map<String, Object> getBasicSurvey(String userId) {
        String[] row = CsvUtil.getStatusRow(userId);

        return Map.of(
                "age_cognition", Integer.parseInt(row[18]),
                "sex", Integer.parseInt(row[19]),
                "race", Integer.parseInt(row[20]),
                "education", Integer.parseInt(row[21])
        );
    }

    /** 전체 설문 상태 조회 */
    public Map<String, Object> getSurveyStatus(String userId) {
        String[] s = CsvUtil.getStatusRow(userId);

        Map<String, Integer> mmseScores = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            mmseScores.put("mmse-" + i, Integer.parseInt(s[4 + i]));
        }

        Map<String, Object> basicInfo = Map.of(
                "age_cognition", Integer.parseInt(s[18]),
                "sex", Integer.parseInt(s[19]),
                "race", Integer.parseInt(s[20]),
                "education", Integer.parseInt(s[21])
        );

        return Map.of(
                "basic_status", s[1],
                "mmse_status", s[2],
                "gds_status", s[3],

                "gds_score", Integer.parseInt(s[4]),
                "mmse_scores", mmseScores,
                "mmse_total", Integer.parseInt(s[17]),
                "basic_survey", basicInfo
        );
    }
}
