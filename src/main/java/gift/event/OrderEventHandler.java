package gift.event;

import gift.service.KakaoMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderEventHandler {

    private static final Logger log = LoggerFactory.getLogger(OrderEventHandler.class);

    private final KakaoMessageService kakaoMessageService;

    public OrderEventHandler(KakaoMessageService kakaoMessageService) {
        this.kakaoMessageService = kakaoMessageService;
    }

    @TransactionalEventListener
    public void handle(OrderCompletedEvent event) {
        try {
            kakaoMessageService.sendOrderMessage(event.getMember(), event.getResponse());
        } catch (Exception e) {
            log.error("카카오 주문 메시지 전송 실패 - memberId: {}, error: {}",
                    event.getMember().getId(),
                    e.getMessage(),
                    e
            );

            // TODO: 실패 메시지 DB에 저장하여 재처리 가능하게 하기
            // TODO: 관리자에게 Slack 또는 Email 알림 전송
        }
    }
}
