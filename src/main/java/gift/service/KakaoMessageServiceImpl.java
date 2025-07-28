package gift.service;

import gift.dto.OrderResponseDto;
import gift.entity.Member;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoMessageServiceImpl implements KakaoMessageService {

    private static final String KAKAO_API_URL = "https://kapi.kakao.com/v2/api/talk/memo/default/send";

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void sendOrderMessageToMe(Member member, OrderResponseDto order) {
        String accessToken = member.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String templateObject = buildTemplate(order);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("template_object", templateObject);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(KAKAO_API_URL, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("카카오 메시지 전송 실패: " + response.getBody());
        }
    }

    private String buildTemplate(OrderResponseDto order) {
        return """
        {
            "object_type": "text",
            "text": "[주문 완료]\\n상품 옵션 ID: %d\\n수량: %d\\n메시지: %s",
            "link": {
                "web_url": "http://localhost:8080",
                "mobile_web_url": "http://localhost:8080"
            },
            "button_title": "확인"
        }
        """.formatted(order.optionId(), order.quantity(), order.message());
    }
}
