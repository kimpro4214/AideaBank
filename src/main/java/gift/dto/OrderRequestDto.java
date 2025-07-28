package gift.dto;

public record OrderRequestDto(
        Long optionId,
        int quantity,
        String message
) {
    public OrderRequestDto {
        if (optionId == null) throw new IllegalArgumentException("optionId는 null일 수 없습니다.");
        if (quantity < 1) throw new IllegalArgumentException("quantity는 1 이상이어야 합니다.");
        if (message == null || message.isBlank()) throw new IllegalArgumentException("message는 비어 있을 수 없습니다.");
    }
}

