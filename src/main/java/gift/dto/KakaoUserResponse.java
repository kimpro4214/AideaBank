package gift.dto;

import java.util.Map;

public record KakaoUserResponse(
        Long id,
        String connected_at,
        Map<String, Object> kakao_account
) {}
