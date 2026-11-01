package com.springboot.online_store.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springboot.online_store.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Table(name = "products",
        check = {
        @CheckConstraint(
                name = "chk_product_price",
                constraint = "price >= 0.1"
        ),
                @CheckConstraint(
                        name = "chk_product_quantity",
                        constraint = "quantity >= 0 AND quantity <= 5000"
                )
        }

)
@Entity
@NoArgsConstructor
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "created_time", nullable = false, updatable = false)
    private LocalDateTime createdTime;

    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<CartItem> cartItem = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProductStatus status;

    public Product(String name, BigDecimal price, int quantity, String imageUrl,
                   LocalDateTime createdTime, LocalDateTime updatedTime, Category category,
                   ProductStatus status) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
        this.category = category;
        this.status = status;
    }

    public boolean isActive(){
        return status == ProductStatus.ACTIVE;
    }
}

