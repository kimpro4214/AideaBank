package gift.controller;

import gift.config.KakaoProperties;
import gift.dto.KakaoTokenResponse;
import gift.service.KakaoAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.net.URI;

@RestController
@RequestMapping("/auth/kakao")
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;
    private final KakaoProperties properties;


    public KakaoAuthController(KakaoAuthService kakaoAuthService, KakaoProperties properties) {
        this.kakaoAuthService = kakaoAuthService;
        this.properties = properties;
    }

    @GetMapping("/login")
    public ResponseEntity<Void> redirectToKakaoLogin() {
        URI redirectUri = URI.create(
                "https://kauth.kakao.com/oauth/authorize" +
                        "?client_id=" + properties.getClientId() +
                        "&redirect_uri=" + properties.getRedirectUri() +
                        "&response_type=code"
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(redirectUri);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/callback")
    public ResponseEntity<String> kakaoCallback(@RequestParam String code) {
        KakaoTokenResponse tokenResponse = kakaoAuthService.getAccessToken(code);
        String jwt = kakaoAuthService.loginAndGenerateToken(tokenResponse.accessToken());
        return ResponseEntity.ok(jwt);
    }

}
