package gift.entity;

import gift.dto.ProductRequestDto;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 15)
    private String name;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private Integer price;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOption> options = new ArrayList<>();

    protected Product() {
    }

    public Product(String name, String imageUrl, Integer price) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
    }

    public static Product from(ProductRequestDto dto) {
        return new Product(dto.name(), dto.imageUrl(), dto.price());
    }

    public void update(String name, String imageUrl, Integer price) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
    }

    public void addOption(ProductOption option) {
        boolean duplicated = options.stream()
                .anyMatch(existing -> existing.getName().equals(option.getName()));
        if (duplicated) {
            throw new IllegalArgumentException("동일한 이름의 옵션이 이미 존재합니다.");
        }

        this.options.add(option);
        option.assignTo(this);
    }


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Integer getPrice() {
        return price;
    }

    public List<ProductOption> options() {
        return List.copyOf(options);
    }
}
