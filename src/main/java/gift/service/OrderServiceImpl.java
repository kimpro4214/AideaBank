package gift.service.order;

import gift.dto.OrderRequestDto;
import gift.dto.OrderResponseDto;
import gift.entity.Member;
import gift.entity.Order;
import gift.entity.ProductOption;
import gift.event.OrderCompletedEvent;
import gift.event.OrderEventPublisher;
import gift.repository.MemberRepository;
import gift.repository.OrderRepository;
import gift.repository.ProductOptionRepository;
import gift.repository.WishRepository;
import gift.service.MemberService;
import gift.service.OrderService;
import gift.service.KakaoMessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;


@Service
public class OrderServiceImpl implements OrderService {

    private final ProductOptionRepository optionRepository;
    private final OrderRepository orderRepository;
    private final WishRepository wishRepository;
    private final KakaoMessageService kakaoMessageService;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final ApplicationEventPublisher eventPublisher;
    private final OrderEventPublisher orderEventPublisher;



    public OrderServiceImpl(ProductOptionRepository optionRepository,
                            OrderRepository orderRepository,
                            WishRepository wishRepository,
                            KakaoMessageService kakaoMessageService,
                            MemberRepository memberRepository,
                            MemberService memberService,
                            ApplicationEventPublisher eventPublisher,
                            OrderEventPublisher orderEventPublisher) {
        this.optionRepository = optionRepository;
        this.orderRepository = orderRepository;
        this.wishRepository = wishRepository;
        this.kakaoMessageService = kakaoMessageService;
        this.memberRepository = memberRepository;
        this.memberService = memberService;
        this.eventPublisher = eventPublisher;
        this.orderEventPublisher = orderEventPublisher;
    }

    @Override
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto request, Long memberId) {
        Member member = memberService.getById(memberId);

        ProductOption option = optionRepository.findById(request.optionId())
                .orElseThrow(() -> new IllegalArgumentException("상품 옵션을 찾을 수 없습니다."));

        option.subtract(request.quantity());

        wishRepository.deleteByMemberAndProduct(member, option.getProduct());

        Order order = Order.create(option, member, request.quantity(), request.message());
        orderRepository.save(order);

        OrderResponseDto response = new OrderResponseDto(
                order.getId(),
                option.getId(),
                order.getQuantity(),
                order.getOrderDateTime(),
                order.getMessage()
        );

        orderEventPublisher.publishOrderCompletedEvent(member, response);

        return response;
    }
}
