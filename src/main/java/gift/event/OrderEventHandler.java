package gift.event;

import gift.service.KakaoMessageService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderEventHandler {

    private final KakaoMessageService kakaoMessageService;

    public OrderEventHandler(KakaoMessageService kakaoMessageService) {
        this.kakaoMessageService = kakaoMessageService;
    }

    @TransactionalEventListener
    public void handle(OrderCompletedEvent event) {
        kakaoMessageService.sendOrderMessageToMe(event.getMember(), event.getResponse());
    }
}
