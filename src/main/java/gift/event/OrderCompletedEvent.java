package gift.event;

import gift.dto.OrderResponseDto;
import gift.entity.Member;

public class OrderCompletedEvent {
    private final Member member;
    private final OrderResponseDto response;

    public OrderCompletedEvent(Member member, OrderResponseDto response) {
        this.member = member;
        this.response = response;
    }

    public Member getMember() {
        return member;
    }

    public OrderResponseDto getResponse() {
        return response;
    }
}
