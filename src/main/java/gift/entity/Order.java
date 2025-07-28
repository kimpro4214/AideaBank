package gift.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "option_id", nullable = false)
    private ProductOption option;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private int quantity;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private LocalDateTime orderDateTime;

    protected Order() {
    }

    private Order(ProductOption option, Long memberId, int quantity, String message) {
        this.option = option;
        this.memberId = memberId;
        this.quantity = quantity;
        this.message = message;
        this.orderDateTime = LocalDateTime.now();
    }

    public static Order create(ProductOption option, Long memberId, int quantity, String message) {
        if (option == null) throw new IllegalArgumentException("상품 옵션은 필수입니다.");
        if (memberId == null) throw new IllegalArgumentException("주문자 ID는 필수입니다.");
        if (quantity <= 0) throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        return new Order(option, memberId, quantity, message);
    }

    public Long getId() {
        return id;
    }

    public ProductOption getOption() {
        return option;
    }

    public Long getMemberId() {
        return memberId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }
}
