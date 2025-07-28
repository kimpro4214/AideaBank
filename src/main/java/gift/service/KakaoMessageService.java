package gift.service;

import gift.dto.OrderResponseDto;
import gift.entity.Member;

public interface KakaoMessageService {
    void sendOrderMessageToMe(Member member, OrderResponseDto order);
}
