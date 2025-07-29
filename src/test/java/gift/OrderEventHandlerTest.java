package gift;

import gift.dto.OrderResponseDto;
import gift.entity.Member;
import gift.entity.Role;
import gift.event.OrderCompletedEvent;
import gift.service.KakaoMessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@SpringBootTest
class OrderEventHandlerTest {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @MockBean
    private KakaoMessageService kakaoMessageService;

    @Test
    void 주문_완료_이벤트_수신_후_메시지_전송이_실행된다() {
        Member member = new Member("test@example.com", "hashedPassword", Role.USER);
        member.updateAccessToken("mock-access-token");

        OrderResponseDto response = new OrderResponseDto(
                1L,
                2L,
                3,
                LocalDateTime.now(),
                "감사합니다"
        );

        eventPublisher.publishEvent(new OrderCompletedEvent(member, response));

        verify(kakaoMessageService, timeout(1000)).sendOrderMessageToMe(eq(member), eq(response));
    }
}
