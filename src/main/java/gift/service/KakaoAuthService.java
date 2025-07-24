package gift.service;

import gift.config.KakaoProperties;
import gift.dto.KakaoTokenResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoAuthService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final KakaoProperties properties;

    public KakaoAuthService(KakaoProperties properties) {
        this.properties = properties;
    }

    public KakaoTokenResponse getAccessToken(String authorizationCode) {
        String url = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", properties.getClientId());
        body.add("redirect_uri", properties.getRedirectUri());
        body.add("code", authorizationCode);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

        try {
            ResponseEntity<KakaoTokenResponse> response = restTemplate.postForEntity(
                    url, request, KakaoTokenResponse.class
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new KakaoAuthException("카카오 인증 실패: " + e.getResponseBodyAsString());
        }
    }

    public class KakaoAuthException extends RuntimeException {
        public KakaoAuthException(String message) {
            super(message);
        }
    }
}
