package gift;

import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Role;
import gift.entity.Wish;
import gift.repository.MemberRepository;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WishRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WishRepository wishRepository;

    @Test
    void save() {
        Member member = new Member("wish@example.com", "pw123", Role.USER);
        memberRepository.save(member);

        Product product = new Product("mouse", "http://image.com/mouse", 20000);
        productRepository.save(product);

        Wish wish = new Wish(member, product);
        Wish saved = wishRepository.save(wish);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getMember().getEmail()).isEqualTo("wish@example.com");
        assertThat(saved.getProduct().getName()).isEqualTo("mouse");
    }
}
