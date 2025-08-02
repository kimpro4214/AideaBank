package gift.controller;

import gift.config.KakaoProperties;
import gift.dto.KakaoTokenResponse;
import gift.service.KakaoAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
    public void redirectToKakaoLogin(HttpServletResponse response) throws IOException {
        String redirectUri =
                "https://kauth.kakao.com/oauth/authorize" +
                        "?client_id=" + properties.getClientId() +
                        "&redirect_uri=" + properties.getRedirectUri() +
                        "&response_type=code";

        response.sendRedirect(redirectUri);
    }

    @GetMapping("/callback")
    public void kakaoCallback(@RequestParam String code, HttpServletResponse response) throws IOException {
        KakaoTokenResponse tokenResponse = kakaoAuthService.getAccessToken(code);
        String jwt = kakaoAuthService.loginAndGenerateToken(tokenResponse.accessToken());

        Cookie cookie = new Cookie("jwtToken", jwt);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        response.sendRedirect("/");
    }
}
