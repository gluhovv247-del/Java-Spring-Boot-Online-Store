package com.springboot.online_store.controllers;

import com.springboot.online_store.dtos.ProductCreatingDto;
import com.springboot.online_store.dtos.ProductInfoDto;
import com.springboot.online_store.entities.Product;
import com.springboot.online_store.services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/product")
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
    public ResponseEntity<ProductInfoDto> createProduct(@RequestBody ProductCreatingDto productDto){
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
            @PathVariable Long id, @RequestBody ProductCreatingDto productDto
            ){
        log.info("update product: {}", productDto);

        return ResponseEntity.ok(productService.updateProduct(id, productDto));
    }
}
