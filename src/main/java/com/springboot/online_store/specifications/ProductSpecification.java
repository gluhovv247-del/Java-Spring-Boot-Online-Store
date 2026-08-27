package com.springboot.online_store.specifications;

import com.springboot.online_store.entities.Category;
import com.springboot.online_store.entities.Product;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductSpecification {
    public static Specification<Product> hasName(String name){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("name"),'%' + name + '%'));
    }

    public static Specification<Product> priceBetween(BigDecimal minPrice, BigDecimal maxPrice){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("price"), minPrice, maxPrice));
    }

    public static Specification<Product> hasCategory(Category category){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("category"), category));
    }
}
