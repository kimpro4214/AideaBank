package gift.event;

import gift.dto.OrderResponseDto;
import gift.entity.Member;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {

    private final ApplicationEventPublisher publisher;

    public OrderEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publishOrderCompletedEvent(Member member, OrderResponseDto response) {
        publisher.publishEvent(new OrderCompletedEvent(member, response));
    }
}
