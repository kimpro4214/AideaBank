package gift;

import gift.dto.OrderRequestDto;
import gift.dto.OrderResponseDto;
import gift.entity.*;
import gift.repository.MemberRepository;
import gift.repository.OrderRepository;
import gift.repository.ProductOptionRepository;
import gift.repository.WishRepository;
import gift.service.KakaoMessageService;
<<<<<<< HEAD
import gift.service.MemberService;
import gift.service.order.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
=======
import gift.service.order.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
>>>>>>> e97bd099681797692d4e965b1bd26e1381b0b9de

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    private ProductOptionRepository optionRepository;
    private OrderRepository orderRepository;
    private WishRepository wishRepository;
<<<<<<< HEAD
    private MemberRepository memberRepository;
    private KakaoMessageService kakaoMessageService;
    private MemberService memberService;
    private ApplicationEventPublisher eventPublisher;
=======
    private KakaoMessageService kakaoMessageService;
    private MemberRepository memberRepository;

>>>>>>> e97bd099681797692d4e965b1bd26e1381b0b9de

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        optionRepository = mock(ProductOptionRepository.class);
        orderRepository = mock(OrderRepository.class);
        wishRepository = mock(WishRepository.class);
<<<<<<< HEAD
        kakaoMessageService = mock(KakaoMessageService.class); // 실제 메시지 전송은 테스트 X
        memberRepository = mock(MemberRepository.class);
        memberService = mock(MemberService.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
=======
        kakaoMessageService = mock(KakaoMessageService.class);
        memberRepository = mock(MemberRepository.class);

>>>>>>> e97bd099681797692d4e965b1bd26e1381b0b9de

        orderService = new OrderServiceImpl(
                optionRepository,
                orderRepository,
                wishRepository,
                kakaoMessageService,
<<<<<<< HEAD
                memberRepository,
                memberService,
                eventPublisher
=======
                memberRepository
>>>>>>> e97bd099681797692d4e965b1bd26e1381b0b9de
        );
    }

    @Test
<<<<<<< HEAD
    void 주문_생성_성공() {
        // given
=======
    void 주문_생성_성공_및_메시지전송_검증() {
        // Given
>>>>>>> e97bd099681797692d4e965b1bd26e1381b0b9de
        Product product = new Product("상품", "image.jpg", 10000);
        setId(product, 101L);

        ProductOption option = new ProductOption("옵션", 10);
        option.assignTo(product);
        setId(option, 1L);

        Member member = new Member("a@a.com", "1234", Role.USER);
        setId(member, 1L);
        member.updateAccessToken("kakao-access-token");

        OrderRequestDto request = new OrderRequestDto(1L, 2, "잘 부탁드립니다.");

<<<<<<< HEAD
        // mocking
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member)); // 실제로 사용하지 않더라도 mock 필요
        when(memberService.getById(1L)).thenReturn(member); // ★ 중요 ★
=======
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
>>>>>>> e97bd099681797692d4e965b1bd26e1381b0b9de
        when(optionRepository.findById(1L)).thenReturn(Optional.of(option));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            setId(order, 100L);
            return order;
        });

<<<<<<< HEAD
        // when
        OrderResponseDto response = orderService.createOrder(request, 1L);

        // then
=======
        // When
        OrderResponseDto response = orderService.createOrder(request, 1L);

        // Then
>>>>>>> e97bd099681797692d4e965b1bd26e1381b0b9de
        assertThat(response.optionId()).isEqualTo(1L);
        assertThat(response.quantity()).isEqualTo(2);
        assertThat(response.message()).isEqualTo("잘 부탁드립니다.");
        assertThat(response.orderDateTime()).isBeforeOrEqualTo(LocalDateTime.now());

<<<<<<< HEAD
        verify(memberService).getById(1L);
        verify(optionRepository).findById(1L);
        verify(wishRepository).deleteByMemberAndProduct(member, product);
        verify(orderRepository).save(any(Order.class));
=======
        verify(memberRepository).findById(1L);
        verify(optionRepository).findById(1L);
        verify(wishRepository).deleteByMemberAndProduct(member, product);
        verify(orderRepository).save(any(Order.class));
        verify(kakaoMessageService).sendOrderMessageToMe(eq(member), any(OrderResponseDto.class));
>>>>>>> e97bd099681797692d4e965b1bd26e1381b0b9de
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
