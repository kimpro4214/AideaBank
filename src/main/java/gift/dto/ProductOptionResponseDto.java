package gift.dto;

public record ProductOptionResponseDto(
        Long id,
        String name,
        int quantity
) {
    public static ProductOptionResponseDto from(gift.entity.ProductOption option) {
        return new ProductOptionResponseDto(
                option.getId(),
                option.getName(),
                option.getQuantity()
        );
    }
}
