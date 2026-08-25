package com.springboot.online_store.services;

import com.springboot.online_store.dtos.ProductCreatingDto;
import com.springboot.online_store.dtos.ProductInfoDto;
import com.springboot.online_store.dtos.ProductMapper;
import com.springboot.online_store.entities.Product;
import com.springboot.online_store.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper mapper;

    public ProductService(ProductRepository productRepository, ProductMapper mapper) {
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    public ProductInfoDto getProduct(Long id) {
        var product = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return mapper.productInfoMapping(product);
    }

    public ProductInfoDto createProduct(ProductCreatingDto productDto) {
        var product = new Product(
                productDto.name(),
                productDto.price(),
                productDto.quantity(),
                productDto.imageUrl(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                productDto.category()
        );
        productRepository.save(product);
        return mapper.productInfoMapping(product);
    }

    public void deleteProduct(Long id) {
        var product = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        productRepository.delete(product);
    }

    public ProductInfoDto updateProduct(Long id, ProductCreatingDto productDto) {
        var product = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        product.setName(productDto.name());
        product.setPrice(productDto.price());
        product.setQuantity(productDto.quantity());
        product.setImageUrl(productDto.imageUrl());
        product.setCategory(productDto.category());
        product.setUpdatedTime(LocalDateTime.now());

        productRepository.save(product);
        return mapper.productInfoMapping(product);
    }
}
