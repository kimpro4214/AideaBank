package gift.controller;

<<<<<<< HEAD
import gift.auth.LoginMember;
import gift.auth.LoginMemberInfoDto;
=======
>>>>>>> e97bd099681797692d4e965b1bd26e1381b0b9de
import gift.dto.OrderRequestDto;
import gift.dto.OrderResponseDto;
import gift.entity.Member;
import gift.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @RequestBody OrderRequestDto request,
            @LoginMember LoginMemberInfoDto loginMember
    ) {
        Long memberId = loginMember.id();
        OrderResponseDto response = orderService.createOrder(request, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}

