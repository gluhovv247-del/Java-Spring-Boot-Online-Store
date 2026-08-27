package com.springboot.online_store.controllers;

import com.springboot.online_store.dtos.CreateAndUpdateProductDto;
import com.springboot.online_store.dtos.ProductFilter;
import com.springboot.online_store.dtos.ProductInfoDto;
import com.springboot.online_store.dtos.ProductSearchFilter;
import com.springboot.online_store.services.ProductService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ProductInfoDto> getProduct(@PathVariable("id") Long id){
        log.info("get product with id: {}", id);
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @PostMapping
    public ResponseEntity<ProductInfoDto> createProduct(@Valid @RequestBody CreateAndUpdateProductDto productDto){
        log.info("create product: {}", productDto);
        return ResponseEntity.ok(productService.createProduct(productDto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id){
        log.info("delete product with id: {}", id);

        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("update/{id}")
    public ResponseEntity<ProductInfoDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody CreateAndUpdateProductDto productDto
            ){
        log.info("update product: {}", productDto);

        return ResponseEntity.ok(productService.updateProduct(id, productDto));
    }

    @GetMapping
    public ResponseEntity<List<ProductInfoDto>> getProductsCatalog(
            ProductFilter productFilter
    ){
        log.info("get products catalog");
        return ResponseEntity.ok(productService.getCatalog(productFilter));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductInfoDto>> getProductsBySearch(
            ProductSearchFilter filter
    ){
        log.info("get products with filter");
        return ResponseEntity.ok(productService.searchByFilter(filter));
    }

}
