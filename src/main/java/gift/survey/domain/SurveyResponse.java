package gift.survey.domain;

import gift.survey.domain.DemographyResponse;
import gift.survey.domain.KaidlResponse;
import gift.survey.domain.SocialResponse;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "survey_responses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private DemographyResponse demography;

    @Embedded
    private SocialResponse social;

    @Embedded
    private KaidlResponse kAidl;

    private LocalDateTime submittedAt;
}

