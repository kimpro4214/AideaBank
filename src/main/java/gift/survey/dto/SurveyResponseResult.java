package gift.survey.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SurveyResponseResult {
    private String status;
    private String message;
    private String responseId;
    private String submittedAt;
}
