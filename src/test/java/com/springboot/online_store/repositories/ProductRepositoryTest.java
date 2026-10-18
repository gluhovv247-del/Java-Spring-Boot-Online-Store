package com.springboot.online_store.repositories;

import com.springboot.online_store.entities.Category;
import com.springboot.online_store.entities.Product;
import com.springboot.online_store.specifications.ProductSpecification;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Nested
    class SpecificationTest{
        @Test
        void nameFilterShouldWork() {
            String searchName = "ph";

            var category = new Category("name", null);
            entityManager.persist(category);

            var targetProduct = new Product(
                "phone",
                    BigDecimal.valueOf(1000),
                    100,
                    null,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    category
            );
            var incompleteProduct = new Product(
                    "ph",
                    BigDecimal.valueOf(1000),
                    100,
                    null,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    category
            );
            var extraProduct = new Product(
                    "abc",
                    BigDecimal.valueOf(1000),
                    100,
                    null,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                   category
            );

            entityManager.persist(targetProduct);
            entityManager.persist(incompleteProduct);
            entityManager.persist(extraProduct);

            entityManager.flush();

            Specification<Product> spec = Specification.unrestricted();
            spec = spec.and(ProductSpecification.hasName(searchName));

            List<Product> result = productRepository.findAll(spec);

            assertEquals(result.getFirst().getName(), targetProduct.getName());
            assertEquals(result.getLast().getName(), incompleteProduct.getName());
        }

        @Test
        void priceFilterShouldWork(){
            var minPrice = BigDecimal.valueOf(150);
            var maxPrice = BigDecimal.valueOf(300);

            var category = new Category("name", null);
            entityManager.persist(category);

            var targetProduct = new Product(
                    "target",
                    BigDecimal.valueOf(200),
                    100,
                    null,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    category
            );
            var cheapProduct = new Product(
                    "cheap",
                    new BigDecimal(100),
                    100,
                    null,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    category
            );
            var expensiveProduct = new Product(
                    "expensive",
                    new BigDecimal(500),
                    100,
                    null,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    category
            );
            entityManager.persist(targetProduct);
            entityManager.persist(cheapProduct);
            entityManager.persist(expensiveProduct);

            entityManager.flush();

            Specification<Product> spec = Specification.unrestricted();
            spec = spec.and(ProductSpecification.priceBetween(minPrice, maxPrice));

            List<Product> result = productRepository.findAll(spec);

            assertEquals(result.getFirst().getName(), targetProduct.getName());

        }

        @Test
        void categoryFilter(){
            var targetCategory = new Category("target", null);
            var wrongCategory = new Category("wrong", null);

            entityManager.persist(targetCategory);
            entityManager.persist(wrongCategory);

            var targetProduct = new Product(
                    "target",
                    BigDecimal.valueOf(200),
                    100,
                    null,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    targetCategory
            );
            var wrongProduct = new Product(
                    "wrong",
                    new BigDecimal(100),
                    100,
                    null,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    wrongCategory
            );

            entityManager.persist(targetProduct);
            entityManager.persist(wrongProduct);

            entityManager.flush();

            Specification<Product> spec = Specification.unrestricted();
            spec = spec.and(ProductSpecification.hasCategory(targetCategory));

            List<Product> result = productRepository.findAll(spec);

            assertEquals(result.getFirst().getName(), targetProduct.getName());
        }
    }

}