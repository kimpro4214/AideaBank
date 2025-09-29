package gift.survey.domain;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class SocialResponse {
    private String occupation;
    private String socialActivity;
    private String familySupport;
    private String exercise;
}
