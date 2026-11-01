package com.springboot.online_store.repositories;

import com.springboot.online_store.constants.BusinessConstants;
import com.springboot.online_store.entities.Category;
import com.springboot.online_store.entities.Product;
import com.springboot.online_store.enums.ProductStatus;
import com.springboot.online_store.enums.SortType;
import com.springboot.online_store.sorting.ProductSort;
import com.springboot.online_store.specifications.ProductSpecification;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    @Container
    @ServiceConnection
    static org.testcontainers.postgresql.PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:15.18-trixie");

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
                    category,
                    ProductStatus.ACTIVE
            );
            var incompleteProduct = new Product(
                    "ph",
                    BigDecimal.valueOf(1000),
                    100,
                    null,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    category,
                    ProductStatus.ACTIVE
            );
            var extraProduct = new Product(
                    "abc",
                    BigDecimal.valueOf(1000),
                    100,
                    null,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    category,
                    ProductStatus.ACTIVE
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
                    category,
                    ProductStatus.ACTIVE
            );
            var cheapProduct = new Product(
                    "cheap",
                    new BigDecimal(100),
                    100,
                    null,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    category,
                    ProductStatus.ACTIVE
            );
            var expensiveProduct = new Product(
                    "expensive",
                    new BigDecimal(500),
                    100,
                    null,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    category,
                    ProductStatus.ACTIVE
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
                    targetCategory,
                    ProductStatus.ACTIVE
            );
            var wrongProduct = new Product(
                    "wrong",
                    new BigDecimal(100),
                    100,
                    null,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    wrongCategory,
                    ProductStatus.ACTIVE
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

    @Nested
    class SortingTest{
        @Test
        void getCatalogShouldSortProductsByPriceAscending() {
            var category = new Category("name", null);
            entityManager.persist(category);
            var sort = new ProductSort();

            var productPrice200 = new Product("Laptop",
                            BigDecimal.valueOf(200),
                            100,
                            null,
                            LocalDateTime.now(),
                            LocalDateTime.now(),
                            category,
                            ProductStatus.ACTIVE);
            var productPrice100 = new Product("Mouse",
                            BigDecimal.valueOf(100),
                            100,
                            null,
                            LocalDateTime.now(),
                            LocalDateTime.now(),
                            category,
                            ProductStatus.ACTIVE);
            var productPrice400 = new Product("Keyboard",
                            BigDecimal.valueOf(400),
                            100,
                            null,
                            LocalDateTime.now(),
                            LocalDateTime.now(),
                            category,
                            ProductStatus.ACTIVE);

            entityManager.persist(productPrice200);
            entityManager.persist(productPrice100);
            entityManager.persist(productPrice400);

            entityManager.flush();

            var sorting = sort.getSort(SortType.PRICE_ASC);

            var pageable = PageRequest.of(
                    BusinessConstants.DEFAULT_PAGE_NUMBER,
                    BusinessConstants.DEFAULT_PAGE_SIZE,
                    sorting);


            Page<Product> result = productRepository.findAll(pageable);

            assertThat(result.getContent())
                    .extracting(Product::getPrice)
                    .containsExactly(
                            BigDecimal.valueOf(100),
                            BigDecimal.valueOf(200),
                            BigDecimal.valueOf(400)
                    );
        }

        @Test
        void getCatalogShouldSortProductsByNewest() {
            var category = new Category("name", null);
            entityManager.persist(category);
            var sort = new ProductSort();

            var productOldest = new Product("Laptop",
                    BigDecimal.valueOf(200),
                    100,
                    null,
                    LocalDateTime.of(2026, 9, 15, 12, 0),
                    LocalDateTime.of(2026, 9, 15, 12, 0),
                    category,
                    ProductStatus.ACTIVE);
            var productMedium = new Product("Mouse",
                    BigDecimal.valueOf(200),
                    100,
                    null,
                    LocalDateTime.of(2026, 10, 15, 12, 0),
                    LocalDateTime.of(2026, 10, 15, 12, 0),
                    category,
                    ProductStatus.ACTIVE);
            var productNewest = new Product("Keyboard",
                    BigDecimal.valueOf(200),
                    100,
                    null,
                    LocalDateTime.of(2026, 11, 1, 12, 0),
                    LocalDateTime.of(2026, 11, 1, 12, 0),
                    category,
                    ProductStatus.ACTIVE);

            entityManager.persist(productOldest);
            entityManager.persist(productNewest);
            entityManager.persist(productMedium);

            entityManager.flush();

            var sorting = sort.getSort(SortType.NEWEST);

            var pageable = PageRequest.of(
                    BusinessConstants.DEFAULT_PAGE_NUMBER,
                    BusinessConstants.DEFAULT_PAGE_SIZE,
                    sorting);


            Page<Product> result = productRepository.findAll(pageable);

            assertThat(result.getContent())
                    .extracting(Product::getCreatedTime)
                    .containsExactly(
                            LocalDateTime.of(2026, 11, 1, 12, 0),
                            LocalDateTime.of(2026, 10, 15, 12, 0),
                            LocalDateTime.of(2026, 9, 15, 12, 0)
                    );
        }

        @Test
        void getCatalogShouldSortProductsByName() {
            var category = new Category("name", null);
            entityManager.persist(category);
            var sort = new ProductSort();

            var productNameLaptop = new Product("Laptop",
                    BigDecimal.valueOf(200),
                    100,
                    null,
                    LocalDateTime.of(2026, 9, 15, 12, 0),
                    LocalDateTime.of(2026, 9, 15, 12, 0),
                    category,
                    ProductStatus.ACTIVE);
            var productNameMouse = new Product("Mouse",
                    BigDecimal.valueOf(200),
                    100,
                    null,
                    LocalDateTime.of(2026, 10, 15, 12, 0),
                    LocalDateTime.of(2026, 10, 15, 12, 0),
                    category,
                    ProductStatus.ACTIVE);
            var productNameKeyboard = new Product("Keyboard",
                    BigDecimal.valueOf(200),
                    100,
                    null,
                    LocalDateTime.of(2026, 11, 1, 12, 0),
                    LocalDateTime.of(2026, 11, 1, 12, 0),
                    category,
                    ProductStatus.ACTIVE);

            entityManager.persist(productNameLaptop);
            entityManager.persist(productNameMouse);
            entityManager.persist(productNameKeyboard);

            entityManager.flush();

            var sorting = sort.getSort(SortType.NAME_ASC);

            var pageable = PageRequest.of(
                    BusinessConstants.DEFAULT_PAGE_NUMBER,
                    BusinessConstants.DEFAULT_PAGE_SIZE,
                    sorting);


            Page<Product> result = productRepository.findAll(pageable);

            assertThat(result.getContent())
                    .extracting(Product::getName)
                    .containsExactly(
                            "Keyboard",
                            "Laptop",
                            "Mouse"
                    );
        }
    }


}