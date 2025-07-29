package gift.service;

import gift.dto.OrderResponseDto;
import gift.entity.Member;

public interface KakaoMessageService {
    void sendOrderMessage(Member member, OrderResponseDto order);
}
