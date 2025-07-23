package gift.service;

import gift.dto.ProductOptionRequestDto;
import gift.dto.ProductOptionResponseDto;
import gift.entity.Product;
import gift.entity.ProductOption;
import gift.repository.ProductOptionRepository;
import gift.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductOptionService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;

    public ProductOptionService(ProductRepository productRepository,
                                ProductOptionRepository productOptionRepository) {
        this.productRepository = productRepository;
        this.productOptionRepository = productOptionRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductOptionResponseDto> getOptionsByProductId(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        return product.options().stream()
                .map(ProductOptionResponseDto::from)
                .toList();
    }

    @Transactional
    public void subtractOptionQuantity(Long optionId, int quantityToSubtract) {
        ProductOption option = productOptionRepository.findById(optionId)
                .orElseThrow(() -> new IllegalArgumentException("옵션을 찾을 수 없습니다."));

        option.subtract(quantityToSubtract);
    }

    @Transactional
    public void addOptionToProduct(Long productId, ProductOptionRequestDto requestDto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        ProductOption option = new ProductOption(requestDto.name(), requestDto.quantity());
        option.validate();
        product.addOption(option);
    }
}
