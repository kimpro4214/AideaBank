package gift.dto;

import gift.entity.Wish;

public class WishResponseDto {

    private final Long wishId;
    private final Long productId;
    private final String productName;
    private final String productImageUrl;
    private final String createdAt;

    public WishResponseDto(Wish wish) {
        this.wishId = wish.getId();
        this.productId = wish.getProduct().getId();
        this.productName = wish.getProduct().getName();
        this.productImageUrl = wish.getProduct().getImageUrl();
        this.createdAt = wish.getCreatedAt().toString();
    }

    public Long getWishId() {
        return wishId;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
