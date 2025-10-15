package gift.survey.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class MmseSubmitResponse {
    private Long userId;                        // 사용자 ID
    private String status;                      // 상태 (RECEIVED)
    private String message;                     // 메시지 (총점 표시)
    private Map<String, Integer> scoresByStage; // 스테이지별 점수 (orientation, memory 등)
    private Map<String, List<String>> uploaded; // 업로드된 파일 목록
}
