package gift.survey.dto;

import lombok.Data;

@Data
public class DemographicsRequest {

    private Long userId;

    private Integer PHC_Age_Cognition;
    private Integer PHC_Sex;
    private String  PHC_Race;
    private Integer PHC_Education;
    private String  PTHAND;
    private String  PTMARRY;
    private Integer PTRTYR;
    private String  PTTLANG;
    private String  PTPLANG;
    private String  PTHOME;
}
