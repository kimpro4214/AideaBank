package gift.survey.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class MmseResultResponse {
    private Long userId;
    private String status;
    private String message;
    private String submittedAt;
    private List<String> attachments;
}
