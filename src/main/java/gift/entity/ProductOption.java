package gift.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "product_option")
public class ProductOption {

    private static final int MAX_NAME_LENGTH = 50;
    private static final int MIN_QUANTITY = 1;
    private static final int MAX_QUANTITY = 100_000_000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(nullable = false)
    private int quantity;

    protected ProductOption() {
    }

    public ProductOption(String name, int quantity) {
        validate(name, quantity);
        this.name = name;
        this.quantity = quantity;
    }

    private void validate(String name, int quantity) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("옵션 이름은 비어 있을 수 없습니다.");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("옵션 수량은 0 이상이어야 합니다.");
        }
    }


    public void assignTo(Product product) {
        this.product = product;
    }

    public void subtract(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("차감 수량은 1 이상이어야 합니다.");
        }
        if (quantity < amount) {
            throw new IllegalArgumentException(
                    String.format("재고가 부족합니다. (요청 수량: %d, 현재 수량: %d)", amount, quantity)
            );
        }
        this.quantity -= amount;
    }

    public void validate() {
        validateQuantity();
        validateName();
    }

    private void validateName() {
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("옵션 이름은 최대 " + MAX_NAME_LENGTH + "자까지 가능합니다.");
        }
        if (!name.matches("^[\\w\\s\\(\\)\\[\\]\\+\\-\\&\\/_]+$")) {
            throw new IllegalArgumentException("허용되지 않은 특수 문자가 포함되어 있습니다.");
        }
    }

    private void validateQuantity() {
        if (quantity < MIN_QUANTITY || quantity >= MAX_QUANTITY) {
            throw new IllegalArgumentException(
                    String.format("옵션 수량은 %d 이상 %d 미만이어야 합니다.", MIN_QUANTITY, MAX_QUANTITY));
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public Product getProduct() {
        return product;
    }
}
