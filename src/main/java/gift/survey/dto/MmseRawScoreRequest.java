package gift.survey.dto;

import lombok.Data;
import java.util.Map;

@Data
public class MmseRawScoreRequest {
    private Map<String, Integer> scores;   // "mmse-1":4, "mmse-4":3 ...
    private Integer totalScore;            // 총점
}
