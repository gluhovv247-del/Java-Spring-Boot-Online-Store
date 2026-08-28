package com.springboot.online_store.services;

import com.springboot.online_store.constants.BusinessConstants;
import com.springboot.online_store.dtos.product.*;
import com.springboot.online_store.entities.Category;
import com.springboot.online_store.entities.Product;
import com.springboot.online_store.mappers.ProductMapper;
import com.springboot.online_store.repositories.CategoryRepository;
import com.springboot.online_store.repositories.ProductRepository;
import com.springboot.online_store.specifications.ProductSpecification;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper mapper;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, ProductMapper mapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    public ProductInfoDto getProduct(Long id) {
        var product = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return mapper.toProductInfoDto(product);
    }

    public ProductInfoDto createProduct(CreateAndUpdateProductDto productDto) {
        Category category = productDto.categoryId() != null
                ? categoryRepository.getReferenceById(productDto.categoryId())
                : null;

        var product = new Product(
                productDto.name(),
                productDto.price(),
                productDto.quantity(),
                productDto.imageUrl(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                category
        );
        productRepository.save(product);
        return mapper.toProductInfoDto(product);
    }

    public void deleteProduct(Long id) {
        var product = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        productRepository.delete(product);
    }

    @Transactional
    public ProductInfoDto updateProduct(Long id, CreateAndUpdateProductDto productDto) {
        var product = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Category category = productDto.categoryId() != null
                ? categoryRepository.getReferenceById(productDto.categoryId())
                : null;

        product.setName(productDto.name());
        product.setPrice(productDto.price());
        product.setQuantity(productDto.quantity());
        product.setImageUrl(productDto.imageUrl());
        product.setCategory(category);
        product.setUpdatedTime(LocalDateTime.now());

        return mapper.toProductInfoDto(product);
    }

    public List<ProductInfoDto> getCatalog(ProductFilter filter) {
        Specification<Product> specification = Specification.unrestricted();

        if (filter.category() != null) {
            specification =
                    specification.and(ProductSpecification.hasCategory(filter.category()));
        }

        int pageNumber = filter.pageNumber() != null ?
                filter.pageNumber() : BusinessConstants.DEFAULT_PAGE_NUMBER;

        int pageSize = filter.pageSize() != null ?
                filter.pageSize() : BusinessConstants.DEFAULT_PAGE_SIZE;

        var pageable = Pageable.ofSize(pageSize).withPage(pageNumber);
        var products = productRepository.findAll(specification, pageable).toList();

        log.info("received: {} products in catalog", products.size());

        return mapper.toProductInfoDtoList(products);
    }

    public List<ProductInfoDto> searchByFilter(ProductSearchFilter searchFilter){
        Specification<Product> specification = Specification.unrestricted();

        int pageNumber = searchFilter.pageNumber() != null ?
                searchFilter.pageNumber() : BusinessConstants.DEFAULT_PAGE_NUMBER;

        int pageSize = searchFilter.pageSize() != null ?
                searchFilter.pageSize() : BusinessConstants.DEFAULT_PAGE_SIZE;

        specification = fillSpecification(specification, searchFilter);
        var pageable = Pageable.ofSize(pageSize).withPage(pageNumber);
        var products = productRepository.findAll(specification, pageable).toList();

        log.info("received: {} products", products.size());

        return mapper.toProductInfoDtoList(products);

    }

    private Specification<Product> fillSpecification(Specification<Product> spec,
                                                     ProductSearchFilter filter){
        if(filter.name() != null){
            spec = spec.and(ProductSpecification.hasName(filter.name()));
        }
        if(filter.category() != null){
            spec = spec.and(ProductSpecification.hasCategory(filter.category()));
        }
        if(filter.minPrice() != null && filter.maxPrice() != null){
            spec = spec.and(ProductSpecification.priceBetween(filter.minPrice(), filter.maxPrice()));
        }
        return spec;
    }
}
