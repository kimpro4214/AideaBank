package gift.survey.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class MmseQuestionResponse {
    private String version;
    private int totalQuestions;
    private List<MmseQuestionDto> questions;
}
