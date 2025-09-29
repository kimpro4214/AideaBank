package gift.survey.domain;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class DemographyResponse {
    private String age;
    private String gender;
    private String education;
    private String maritalStatus;
}
