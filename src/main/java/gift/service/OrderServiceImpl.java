package gift.service.order;

import gift.dto.OrderRequestDto;
import gift.dto.OrderResponseDto;
import gift.entity.Member;
import gift.entity.Order;
import gift.entity.ProductOption;
import gift.repository.OrderRepository;
import gift.repository.ProductOptionRepository;
import gift.repository.WishRepository;
import gift.service.OrderService;
import gift.service.KakaoMessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

    private final ProductOptionRepository optionRepository;
    private final OrderRepository orderRepository;
    private final WishRepository wishRepository;
    private final KakaoMessageService kakaoMessageService;

    public OrderServiceImpl(ProductOptionRepository optionRepository,
                            OrderRepository orderRepository,
                            WishRepository wishRepository,
                            KakaoMessageService kakaoMessageService) {
        this.optionRepository = optionRepository;
        this.orderRepository = orderRepository;
        this.wishRepository = wishRepository;
        this.kakaoMessageService = kakaoMessageService;
    }

    @Override
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto request, Member member) {
        ProductOption option = optionRepository.findById(request.optionId())
                .orElseThrow(() -> new IllegalArgumentException("상품 옵션을 찾을 수 없습니다."));

        option.subtract(request.quantity());

        wishRepository.deleteByMemberAndProduct(member, option.getProduct());

        Order order = Order.create(option, member.getId(), request.quantity(), request.message());
        orderRepository.save(order);

        OrderResponseDto response = new OrderResponseDto(
                order.getId(),
                option.getId(),
                order.getQuantity(),
                order.getOrderDateTime(),
                order.getMessage()
        );

        kakaoMessageService.sendOrderMessageToMe(member, response);

        return response;
    }
}
