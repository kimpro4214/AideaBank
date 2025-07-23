package gift.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductOptionTest {

    @Test
    void 이름이_50자를_초과하면_예외가_발생한다() {
        String name = "A".repeat(51);
        ProductOption option = new ProductOption(name, 10);

        assertThatThrownBy(option::validate)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("최대 50자");
    }

    @Test
    void 허용되지_않은_특수문자가_포함되면_예외가_발생한다() {
        ProductOption option = new ProductOption("손크림@립밤", 10);

        assertThatThrownBy(option::validate)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("특수 문자");
    }

    @Test
    void 수량이_0이면_예외가_발생한다() {
        ProductOption option = new ProductOption("정상", 0);

        assertThatThrownBy(option::validate)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("1 이상 1억 미만");
    }

    @Test
    void 수량이_1억_이상이면_예외가_발생한다() {
        ProductOption option = new ProductOption("정상", 100_000_000);

        assertThatThrownBy(option::validate)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("1 이상 1억 미만");
    }

    @Test
    void subtract_정상_차감_된다() {
        ProductOption option = new ProductOption("정상", 10);
        option.subtract(3);

        assertThat(option.getQuantity()).isEqualTo(7);
    }

    @Test
    void subtract_수량이_0이면_예외가_발생한다() {
        ProductOption option = new ProductOption("정상", 10);

        assertThatThrownBy(() -> option.subtract(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("차감 수량은 1 이상");
    }

    @Test
    void subtract_수량이_재고보다_많으면_예외가_발생한다() {
        ProductOption option = new ProductOption("정상", 2);

        assertThatThrownBy(() -> option.subtract(3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("재고가 부족");
    }

    @Test
    void 옵션_이름이_중복되면_예외가_발생한다() {
        Product product = new Product("상품", "이미지", 10000);
        ProductOption option1 = new ProductOption("01. 시어버터", 100);
        ProductOption option2 = new ProductOption("01. 시어버터", 50); // 같은 이름

        product.addOption(option1);

        assertThatThrownBy(() -> product.addOption(option2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재");
    }
}
