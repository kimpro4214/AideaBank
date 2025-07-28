package gift;

import gift.dto.OrderRequestDto;
import gift.dto.OrderResponseDto;
import gift.entity.*;
import gift.repository.MemberRepository;
import gift.repository.OrderRepository;
import gift.repository.ProductOptionRepository;
import gift.repository.WishRepository;
import gift.service.KakaoMessageService;
import gift.service.order.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    private ProductOptionRepository optionRepository;
    private OrderRepository orderRepository;
    private WishRepository wishRepository;
    private KakaoMessageService kakaoMessageService;
    private MemberRepository memberRepository;


    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        optionRepository = mock(ProductOptionRepository.class);
        orderRepository = mock(OrderRepository.class);
        wishRepository = mock(WishRepository.class);
        kakaoMessageService = mock(KakaoMessageService.class);
        memberRepository = mock(MemberRepository.class);


        orderService = new OrderServiceImpl(
                optionRepository,
                orderRepository,
                wishRepository,
                kakaoMessageService,
                memberRepository
        );
    }

    @Test
    void 주문_생성_성공_및_메시지전송_검증() {
        // Given
        Product product = new Product("상품", "image.jpg", 10000);
        setId(product, 101L);

        ProductOption option = new ProductOption("옵션", 10);
        option.assignTo(product);
        setId(option, 1L);

        Member member = new Member("a@a.com", "1234", Role.USER);
        setId(member, 1L);
        member.updateAccessToken("kakao-access-token");

        OrderRequestDto request = new OrderRequestDto(1L, 2, "잘 부탁드립니다.");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(optionRepository.findById(1L)).thenReturn(Optional.of(option));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            setId(order, 100L);
            return order;
        });

        // When
        OrderResponseDto response = orderService.createOrder(request, 1L);

        // Then
        assertThat(response.optionId()).isEqualTo(1L);
        assertThat(response.quantity()).isEqualTo(2);
        assertThat(response.message()).isEqualTo("잘 부탁드립니다.");
        assertThat(response.orderDateTime()).isBeforeOrEqualTo(LocalDateTime.now());

        verify(memberRepository).findById(1L);
        verify(optionRepository).findById(1L);
        verify(wishRepository).deleteByMemberAndProduct(member, product);
        verify(orderRepository).save(any(Order.class));
        verify(kakaoMessageService).sendOrderMessageToMe(eq(member), any(OrderResponseDto.class));
    }

    private void setId(Object entity, Long id) {
        try {
            var field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException("ID 설정 실패", e);
        }
    }
}
