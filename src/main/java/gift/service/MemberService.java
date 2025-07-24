package gift.service;

import gift.auth.JwtProvider;
import gift.entity.Member;
import gift.entity.Role;
import gift.dto.MemberRequestDto;
import gift.exception.forbidden.EmailDuplicateException;
import gift.exception.forbidden.EmailNotFoundException;
import gift.exception.forbidden.WrongPasswordException;
import gift.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public MemberService(MemberRepository memberRepository, JwtProvider jwtProvider) {
        this.memberRepository = memberRepository;
        this.jwtProvider = jwtProvider;
    }

    public void register(MemberRequestDto dto) {
        if (memberRepository.findByEmail(dto.email()).isPresent()) {
            throw new EmailDuplicateException();
        }

        String hashedPassword = BCrypt.hashpw(dto.password(), BCrypt.gensalt());
        Member member = new Member(dto.email(), hashedPassword, Role.USER);
        memberRepository.save(member);
    }

    public String login(MemberRequestDto dto) {
        Member member = memberRepository.findByEmail(dto.email())
                .orElseThrow(EmailNotFoundException::new);

        if (!BCrypt.checkpw(dto.password(), member.getPassword())) {
            throw new WrongPasswordException();
        }

        return jwtProvider.createToken(member);
    }

    public Member findByToken(String token) {
        Claims claims = jwtProvider.parseToken(token);
        Long memberId = Long.parseLong(claims.getSubject());

        return memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
    }
}
