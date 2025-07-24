package gift.controller;

import gift.service.KakaoAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/kakao")
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    public KakaoAuthController(KakaoAuthService kakaoAuthService) {
        this.kakaoAuthService = kakaoAuthService;
    }

    @GetMapping("/callback")
    public ResponseEntity<String> kakaoCallback(@RequestParam String code) {
        var tokenResponse = kakaoAuthService.getAccessToken(code);
        return ResponseEntity.ok("Access Token: " + tokenResponse.access_token());
    }

    @GetMapping("/auth/kakao/user-info")
    public ResponseEntity<String> getUserInfo(@RequestParam String token) {
        var userInfo = kakaoAuthService.getUserInfo(token);
        return ResponseEntity.ok("User ID: " + userInfo.id());
    }
}

