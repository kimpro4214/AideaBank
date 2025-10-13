package gift.survey.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class MmseSubmitResponse {
    private Long userId;
    private String status;
    private String message; // 자동 채점 결과 포함 (ex: 총점 23점)
    private Map<String, List<String>> uploaded;
}
