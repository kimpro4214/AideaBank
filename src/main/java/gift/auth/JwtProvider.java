package gift.auth;

import gift.entity.Member;
import gift.exception.unauthorized.WrongHeaderException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    public String createToken(Member member) {
        return Jwts.builder()
                .subject(Long.toString(member.getId()))
                .claim("email", member.getEmail())
                .claim("role", member.getRole())
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new WrongHeaderException();
        }
    }
}
