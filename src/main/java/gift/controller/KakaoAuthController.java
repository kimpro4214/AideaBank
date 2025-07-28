package gift.controller;

import gift.dto.KakaoTokenResponse;
import gift.service.KakaoAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/kakao")
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    public KakaoAuthController(KakaoAuthService kakaoAuthService) {
        this.kakaoAuthService = kakaoAuthService;
    }

    @GetMapping("/callback")
    public ResponseEntity<String> kakaoCallback(@RequestParam String code) {
        KakaoTokenResponse tokenResponse = kakaoAuthService.getAccessToken(code);
        String jwt = kakaoAuthService.loginAndGenerateToken(tokenResponse.accessToken());
        return ResponseEntity.ok(jwt);
    }

}
