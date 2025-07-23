package gift.service;

import gift.dto.ProductRequestDto;
import gift.dto.ProductResponseDto;
import gift.entity.Product;
import gift.entity.ProductOption;
import gift.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
        return new ProductResponseDto(product);
    }

    public Page<ProductResponseDto> getProductList(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(ProductResponseDto::new);
    }


    public ProductResponseDto addProduct(ProductRequestDto requestDto) {
        Product product = Product.from(requestDto);
        Product savedProduct = productRepository.save(product);
        return new ProductResponseDto(savedProduct);
    }

    @Transactional
    public void addOptionToProduct(Long productId, String name, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        ProductOption option = new ProductOption(name, quantity);
        product.addOption(option);
    }


    @Transactional
    public void updateProduct(Long id, ProductRequestDto requestDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));

        product.update(requestDto.name(), requestDto.imageUrl(), requestDto.price());
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new NoSuchElementException("Product not found with id: " + id);
        }

        productRepository.deleteById(id);
    }
}
