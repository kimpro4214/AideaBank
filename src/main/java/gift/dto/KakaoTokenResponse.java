package gift.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(SnakeCaseStrategy.class)
public record KakaoTokenResponse(
        String accessToken,
        String tokenType,
        String refreshToken,
        long expiresIn,
        String scope,
        long refreshTokenExpiresIn
) {}


