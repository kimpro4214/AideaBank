package gift.survey.dto;

import gift.survey.domain.DemographyResponse;
import gift.survey.domain.KaidlResponse;
import gift.survey.domain.SocialResponse;
import lombok.Data;

@Data
public class SurveyResponseRequest {
    private DemographyResponse demography;
    private SocialResponse social;
    private KaidlResponse kAidl;
}
