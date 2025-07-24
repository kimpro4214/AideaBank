package gift.dto;

public record KakaoTokenResponse(
        String access_token,
        String token_type,
        String refresh_token,
        long expires_in,
        String scope,
        long refresh_token_expires_in
) {}

