package gift.survey.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MmseQuestionDto {
    private Long id;
    private String category;
    private String text;
    private String ttsUrl;
    private String type; // TEXT, AUDIO, VIDEO
}
