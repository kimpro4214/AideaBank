package gift.survey.dto;

import lombok.Data;
import java.util.Map;

@Data
public class MmseScoreRequest {

    private Long userId;

    // mmse-1, mmse-4, mmse-5 ...
    private Map<String, Integer> scores;

    private Integer totalScore;
}
