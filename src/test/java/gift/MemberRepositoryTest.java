package gift;

import gift.entity.Member;
import gift.entity.Role;
import gift.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("회원 정보를 저장하면 ID, 이메일, 역할이 정확히 저장된다")
    @Test
    void save() {
        Member member = new Member("test@example.com", "pw123", Role.USER);

        Member saved = memberRepository.save(member);

        assertAll(
                () -> assertThat(saved.getId()).isNotNull(),
                () -> assertThat(saved.getEmail()).isEqualTo("test@example.com"),
                () -> assertThat(saved.getRole()).isEqualTo(Role.USER)
        );
    }

    @DisplayName("이메일로 회원 정보를 조회할 수 있다")
    @Test
    void findByEmail() {
        Member member = new Member("find@example.com", "pw123", Role.USER);
        memberRepository.save(member);

        Member found = memberRepository.findByEmail("find@example.com")
                .orElseThrow(() -> new IllegalStateException("해당 이메일의 회원이 존재하지 않습니다."));

        assertAll(
                () -> assertThat(found.getEmail()).isEqualTo("find@example.com"),
                () -> assertThat(found.getRole()).isEqualTo(Role.USER)
        );
    }
}
