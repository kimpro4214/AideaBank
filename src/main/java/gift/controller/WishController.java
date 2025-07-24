package gift.controller;

import gift.dto.WishRequestDto;
import gift.dto.WishResponseDto;
import gift.entity.Member;
import gift.service.WishService;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/wishes")
public class WishController {

    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @PostMapping
    public void addWish(@RequestBody WishRequestDto requestDto,
                        @RequestAttribute Member member) {
        wishService.addWish(member.getId(), requestDto.productId());
    }

    @GetMapping
    public Page<WishResponseDto> getWishes(@RequestAttribute Member member,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestParam(defaultValue = "createdAt,desc") String[] sort) {

        Sort sortObj = Sort.by(
                Arrays.stream(sort)
                        .map(s -> {
                            String[] parts = s.split(",");
                            return new Sort.Order(Sort.Direction.fromString(parts[1]), parts[0]);
                        })
                        .toList()
        );

        Pageable pageable = PageRequest.of(page, size, sortObj);
        return wishService.getWishes(member.getId(), pageable);
    }
}
