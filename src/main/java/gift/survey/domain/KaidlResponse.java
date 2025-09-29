package gift.survey.domain;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class KaidlResponse {
    private String phoneUse;
    private String shopping;
    private String mealPreparation;
    private String housekeeping;
    private String laundry;
    private String transportation;
    private String medication;
    private String finance;
}

