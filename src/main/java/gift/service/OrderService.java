package gift.service;

import gift.dto.OrderRequestDto;
import gift.dto.OrderResponseDto;
import gift.entity.Member;

public interface OrderService {
    OrderResponseDto createOrder(OrderRequestDto request, Member member);
}
