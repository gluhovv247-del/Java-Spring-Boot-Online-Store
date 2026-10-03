package com.springboot.online_store.services;

import com.springboot.online_store.constants.BusinessConstants;
import com.springboot.online_store.dtos.product.CreateAndUpdateProductDto;
import com.springboot.online_store.dtos.product.ProductFilter;
import com.springboot.online_store.dtos.product.ProductInfoDto;
import com.springboot.online_store.dtos.product.ProductSearchFilter;
import com.springboot.online_store.entities.Category;
import com.springboot.online_store.entities.Product;
import com.springboot.online_store.mappers.ProductMapper;
import com.springboot.online_store.repositories.CategoryRepository;
import com.springboot.online_store.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductMapper mapper;

    @InjectMocks
    private ProductService productService;

    @Nested
    class GetProductById {
        @Test
        void getProductShouldWork() {
            //GIVEN
            Long id = 1L;
            var product = mock(Product.class);
            var productInfoDto = productInfoDto();

            when(productRepository.findById(id))
                    .thenReturn(Optional.of(product));
            when(mapper.toProductInfoDto(product))
                    .thenReturn(productInfoDto);

            //WHEN
            var result = productService.getProduct(id);

            //THEN
            assertThat(result).isEqualTo(productInfoDto);
            verify(productRepository)
                    .findById(id);
            verify(mapper)
                    .toProductInfoDto(product);
        }
        @Test
        void shouldThrowEntityNotFoundExceptionWithProduct(){
            //GIVEN
            Long id = 1L;
            when(productRepository.findById(id))
                    .thenReturn(Optional.empty());
            //WHEN & THEN
            assertThrows(
                    EntityNotFoundException.class,
                    () -> productService.getProduct(id)
            );

            verify(productRepository)
                    .findById(id);
        }
    }

    @Nested
    class CreateProduct{
        @Test
        void createProductShouldWork(){
            //GIVEN
            var createDto = createAndUpdateProductDto();
            var category = mock(Category.class);
            var product = mock(Product.class);
            var productInfoDto = productInfoDto();
            ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);

            when(mapper.toProductInfoDto(product))
                    .thenReturn(productInfoDto);
            when(categoryRepository.findById(createDto.categoryId()))
                    .thenReturn(Optional.of(category));
            when(productRepository.save(any(Product.class))).thenReturn(product);

            //WHEN
            var result = productService.createProduct(createDto);

            //THEN
            assertThat(result).isEqualTo(productInfoDto);

            verify(categoryRepository)
                    .findById(createDto.categoryId());
            verify(mapper)
                    .toProductInfoDto(product);
            verify(productRepository)
                    .save(captor.capture());

            var capturedProduct = captor.getValue();

            assertThat(capturedProduct.getName()).isEqualTo(createDto.name());
            assertThat(capturedProduct.getPrice()).isEqualTo(createDto.price());
            assertThat(capturedProduct.getCategory()).isSameAs(category);
            assertThat(capturedProduct.getImageUrl()).isEqualTo(createDto.imageUrl());
            assertThat(capturedProduct.getQuantity()).isEqualTo(createDto.quantity());
        }

        @Test
        void shouldThrowEntityNotFoundExceptionWithCategory(){
            //GIVEN
            var createDto = createAndUpdateProductDto();

            when(categoryRepository.findById(createDto.categoryId()))
                    .thenReturn(Optional.empty());
            //WHEN & THEN
            assertThrows(
                    EntityNotFoundException.class,
                    () -> productService.createProduct(createDto)
            );

            verify(categoryRepository)
                    .findById(createDto.categoryId());
            verify(productRepository, never())
                    .save(any(Product.class));
        }
    }

    @Nested
    class GetCatalog{

        @Test
        void getCatalogShouldWork(){
            //GIVEN
            var productFilter = productFilter();
            var productInfoDto = productInfoDto();
            var product = mock(Product.class);
            var pageable = Pageable.ofSize(productFilter.pageSize())
                    .withPage(productFilter.pageNumber());
            var products = List.of(product);
            var dtos = List.of(productInfoDto);

            when(productRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(new PageImpl<>(products));
            when(mapper.toProductInfoDtoList(products))
                    .thenReturn(dtos);

            //WHEN
            var result = productService.getCatalog(productFilter);

            //THEN
            assertThat(result).isEqualTo(dtos);
            verify(productRepository)
                    .findAll(any(Specification.class), eq(pageable));
            verify(mapper)
                    .toProductInfoDtoList(products);
        }

        @Test
        void getCatalogShouldWorkWithoutPageParams(){
            //GIVEN
            var productFilter = ProductFilter.builder()
                            .build();
            var productInfoDto = productInfoDto();
            var product = mock(Product.class);

            var pageable = Pageable.ofSize(BusinessConstants.DEFAULT_PAGE_SIZE)
                    .withPage(BusinessConstants.DEFAULT_PAGE_NUMBER);
            var products = List.of(product);
            var dtos = List.of(productInfoDto);

            when(productRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(new PageImpl<>(products));
            when(mapper.toProductInfoDtoList(products))
                    .thenReturn(dtos);

            //WHEN
            var result = productService.getCatalog(productFilter);

            //THEN
            assertThat(result).isEqualTo(dtos);
            verify(productRepository)
                    .findAll(any(Specification.class), eq(pageable));
            verify(mapper)
                    .toProductInfoDtoList(products);
        }

        @Test
        void getCatalogWithCategoryShouldWork(){
            //GIVEN
            var productInfoDto = productInfoDto();
            var product = mock(Product.class);
            var productFilter = productFilterWithCategory();

            var products = List.of(product);
            var dtos = List.of(productInfoDto);

            when(productRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(products));
            when(mapper.toProductInfoDtoList(products))
                    .thenReturn(dtos);

            //WHEN
            var result = productService.getCatalog(productFilter);

            //THEN
            assertThat(result).isEqualTo(dtos);
            verify(productRepository)
                    .findAll(any(Specification.class), any(Pageable.class));
            verify(mapper)
                    .toProductInfoDtoList(products);
        }
    }

    @Nested
    class SearchByFilter{

        @Test
        void searchByFilterShouldWork(){
            //GIVEN
            var productFilter = productSearchFilter();
            var productInfoDto = mock(ProductInfoDto.class);
            var product = mock(Product.class);
            var pageable = Pageable.ofSize(productFilter.pageSize())
                    .withPage(productFilter.pageNumber());
            var products = List.of(product);
            var dtos = List.of(productInfoDto);

            when(productRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(new PageImpl<>(products));
            when(mapper.toProductInfoDtoList(products))
                    .thenReturn(dtos);

            //WHEN
            var result = productService.searchByFilter(productFilter);

            //THEN
            assertThat(result).isEqualTo(dtos);
            verify(productRepository)
                    .findAll(any(Specification.class), eq(pageable));
            verify(mapper)
                    .toProductInfoDtoList(products);
        }

        @Test
        void searchByFilterWithoutPageParamsShouldWork(){
            //GIVEN
            var productFilter  = ProductSearchFilter.builder().build();
            var productInfoDto = mock(ProductInfoDto.class);
            var product = mock(Product.class);
            var pageable = Pageable.ofSize(BusinessConstants.DEFAULT_PAGE_SIZE)
                    .withPage(BusinessConstants.DEFAULT_PAGE_NUMBER);
            var products = List.of(product);
            var dtos = List.of(productInfoDto);

            when(productRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(new PageImpl<>(products));
            when(mapper.toProductInfoDtoList(products))
                    .thenReturn(dtos);

            //WHEN
            var result = productService.searchByFilter(productFilter);

            //THEN
            assertThat(result).isEqualTo(dtos);
            verify(productRepository)
                    .findAll(any(Specification.class), eq(pageable));
            verify(mapper)
                    .toProductInfoDtoList(products);
        }
    }

    @Nested
    class UpdateProduct {

        @Test
        void updateProductShouldWork() {
            var createDto = createAndUpdateProductDto();
            Long categoryId = createDto.categoryId();
            Long productId = 1L;
            var product = mock(Product.class);
            var category = mock(Category.class);
            var productInfoDto = productInfoDto();

            when(productRepository.findById(productId))
                    .thenReturn(Optional.of(product));
            when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.of(category));
            when(mapper.toProductInfoDto(product))
                    .thenReturn(productInfoDto);

            var result = productService.updateProduct(productId, createDto);

            assertThat(result).isEqualTo(productInfoDto);

            verify(product).setName(createDto.name());
            verify(product).setPrice(createDto.price());
            verify(product).setQuantity(createDto.quantity());
            verify(product).setImageUrl(createDto.imageUrl());
            verify(product).setCategory(category);
            verify(product).setUpdatedTime(any(LocalDateTime.class));

            verify(productRepository)
                    .findById(productId);
            verify(categoryRepository)
                    .findById(categoryId);
            verify(mapper)
                    .toProductInfoDto(product);
        }

        @Test
        void shouldThrowEntityNotFoundExceptionWithProduct(){
            //GIVEN
            Long id = 1L;
            var createDto = createAndUpdateProductDto();
            var product = mock(Product.class);
            var category = mock(Category.class);

            when(productRepository.findById(id))
                    .thenReturn(Optional.empty());
            //WHEN & THEN
            assertThrows(
                    EntityNotFoundException.class,
                    () -> productService.updateProduct(id, createDto)
            );

            verify(productRepository)
                    .findById(id);

            verify(product, never()).setName(createDto.name());
            verify(product, never()).setPrice(createDto.price());
            verify(product, never()).setQuantity(createDto.quantity());
            verify(product, never()).setImageUrl(createDto.imageUrl());
            verify(product, never()).setCategory(category);
            verify(product, never()).setUpdatedTime(any(LocalDateTime.class));
        }
    }

    @Nested
    class DeleteProduct {
        @Test
        void deleteProductShouldWork() {
            //GIVEN
            Long id = 1L;
            var product = mock(Product.class);
            when(productRepository.findById(id))
                    .thenReturn(Optional.of(product));

            //WHEN
            productService.deleteProduct(id);

            //THEN
            verify(productRepository)
                    .delete(product);
            verify(productRepository)
                    .findById(id);

        }
        @Test
        void shouldThrowEntityNotFoundExceptionWithProduct(){
            //GIVEN
            Long id = 1L;
            var product = mock(Product.class);
            when(productRepository.findById(id))
                    .thenReturn(Optional.empty());
            //WHEN & THEN
            assertThrows(
                    EntityNotFoundException.class,
                    () -> productService.deleteProduct(id)
            );

            verify(productRepository)
                    .findById(id);
            verify(productRepository, never())
                    .delete(product);
        }
    }

    private CreateAndUpdateProductDto createAndUpdateProductDto(){
        return CreateAndUpdateProductDto.builder()
                .name("test_product")
                .price(BigDecimal.valueOf(1000))
                .categoryId(0L)
                .quantity(50)
                .imageUrl("fjddjdj")
                .build();
    }

    private ProductInfoDto productInfoDto(){
        return ProductInfoDto.builder()
                .name("test_product")
                .price(BigDecimal.valueOf(1000))
                .quantity(50)
                .imageUrl("fjddjdj")
                .build();
    }

    private ProductFilter productFilter(){
        return ProductFilter.builder()
                .pageNumber(0)
                .pageSize(10)
                .category(null)
                .build();
    }

    private ProductSearchFilter productSearchFilter(){
        return ProductSearchFilter.builder()
                .pageNumber(0)
                .pageSize(10)
                .build();
    }

    private ProductFilter productFilterWithCategory(){
        return ProductFilter.builder()
                .pageNumber(0)
                .pageSize(10)
                .category(new Category("phones", null))
                .build();
    }



}