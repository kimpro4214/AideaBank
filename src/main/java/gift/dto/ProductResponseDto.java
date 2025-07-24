package gift.dto;

import gift.entity.Product;

import java.util.Collections;
import java.util.List;

public class ProductResponseDto {

    private Long id;
    private String name;
    private int price;
    private String imageUrl;
    private List<ProductOptionResponseDto> options;

    public ProductResponseDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.imageUrl = product.getImageUrl();
        this.options = product.options().stream()
                .map(ProductOptionResponseDto::from)
                .toList();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<ProductOptionResponseDto> getOptions() {
        return Collections.unmodifiableList(options);
    }
}
