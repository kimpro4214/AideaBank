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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private int quantity;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private LocalDateTime orderDateTime;

    protected Order() {
    }

    private Order(ProductOption option, Member member, int quantity, String message) {
        this.option = option;
        this.member = member;
        this.quantity = quantity;
        this.message = message;
        this.orderDateTime = LocalDateTime.now();
    }

    public static Order create(ProductOption option, Member member, int quantity, String message) {
        if (option == null) throw new IllegalArgumentException("상품 옵션은 필수입니다.");
        if (member == null) throw new IllegalArgumentException("회원은 필수입니다.");
        if (quantity <= 0) throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        return new Order(option, member, quantity, message);
    }

    public Long getId() {
        return id;
    }

    public ProductOption getOption() {
        return option;
    }

    public Member getMember() {
        return member;
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

