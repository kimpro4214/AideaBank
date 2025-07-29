package gift.service;

import gift.dto.WishResponseDto;
import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Wish;
import gift.repository.MemberRepository;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class WishService {

    private final WishRepository wishRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final MemberService memberService;


    public WishService(WishRepository wishRepository,
                       MemberRepository memberRepository,
                       ProductRepository productRepository,
                       MemberService memberService) {
        this.wishRepository = wishRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
        this.memberService = memberService;
    }

    @Transactional
    public void addWish(Long memberId, Long productId) {
        Member member = memberService.getById(memberId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + productId));

        Wish wish = new Wish(member, product);
        wishRepository.save(wish);
    }

    @Transactional(readOnly = true)
    public Page<WishResponseDto> getWishes(Long memberId, Pageable pageable) {
        Member member = memberService.getById(memberId);

        return wishRepository.findAllByMember(member, pageable)
                .map(WishResponseDto::new);
    }
}
