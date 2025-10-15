package gift.survey.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class MmseResultResponse {
    private Long userId;                        // 사용자 ID
    private String status;                      // 상태 (COMPLETED)
    private String message;                     // 메시지 (자동 채점 완료)
    private String submittedAt;                 // 제출 시각
    private Map<String, Integer> scoresByStage; // 스테이지별 점수 (추가됨)
    private List<String> attachments;           // 첨부 파일 목록
}
