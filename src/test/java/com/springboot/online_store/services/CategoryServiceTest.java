package com.springboot.online_store.services;

import com.springboot.online_store.constants.BusinessConstants;
import com.springboot.online_store.dtos.category.CategoryCreateDto;
import com.springboot.online_store.dtos.category.CategoryInfoDto;
import com.springboot.online_store.entities.Category;
import com.springboot.online_store.entities.Product;
import com.springboot.online_store.exceptions.custom.CategoryNotEmptyException;
import com.springboot.online_store.mappers.CategoryMapper;
import com.springboot.online_store.repositories.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private CategoryCreateDto createDtoWithParent;
    private CategoryCreateDto createDtoWithoutParent;

    @BeforeEach
    void setUp() {
        createDtoWithParent = CategoryCreateDto.builder()
                .name("name")
                .parentId(1L)
                .build();
        createDtoWithoutParent = CategoryCreateDto.builder()
                .name("name")
                .parentId(null)
                .build();
    }

    @Nested
    class CreateCategory {
        @Test
        void createCategoryShouldWorkWithParentId() {
            //GIVEN
            var category = mock(Category.class);
            var parentCategory = mock(Category.class);
            var categoryInfoDto = mock(CategoryInfoDto.class);
            ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);

            when(categoryMapper.toCategoryInfo(any(Category.class)))
                    .thenReturn(categoryInfoDto);
            when(categoryRepository.save(any(Category.class)))
                    .thenReturn(category);
            when(categoryRepository.getReferenceById(createDtoWithParent.parentId()))
                    .thenReturn(parentCategory);

            //WHEN
            var result = categoryService.createCategory(createDtoWithParent);

            //THEN
            assertThat(result).isEqualTo(categoryInfoDto);
            verify(categoryMapper)
                    .toCategoryInfo(any(Category.class));
            verify(categoryRepository)
                    .save(captor.capture());
            verify(categoryRepository)
                    .getReferenceById(createDtoWithParent.parentId());

            var capturedCategory = captor.getValue();
            assertThat(capturedCategory.getName()).isEqualTo(createDtoWithParent.name());
            assertThat(capturedCategory.getParent()).isSameAs(parentCategory);
        }

        @Test
        void createCategoryShouldWorkWithoutParentId() {
            //GIVEN
            var category = mock(Category.class);
            var categoryInfoDto = mock(CategoryInfoDto.class);
            ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);

            when(categoryMapper.toCategoryInfo(any(Category.class)))
                    .thenReturn(categoryInfoDto);
            when(categoryRepository.save(any(Category.class)))
                    .thenReturn(category);

            //WHEN
            var result = categoryService.createCategory(createDtoWithoutParent);

            //THEN
            assertThat(result).isEqualTo(categoryInfoDto);
            verify(categoryMapper)
                    .toCategoryInfo(any(Category.class));
            verify(categoryRepository)
                    .save(captor.capture());

            var capturedCategory = captor.getValue();
            assertThat(capturedCategory.getName()).isEqualTo(createDtoWithParent.name());
            assertThat(capturedCategory.getParent()).isSameAs(null);
        }
    }

    @Nested
    class getCategories{
        @Test
        void getCategoriesShouldWork() {
            var categoryDto = mock(CategoryInfoDto.class);
            var category = mock(Category.class);
            int pageSize = 10;
            int pageNumber = 0;
            var pageable = Pageable.ofSize(pageSize).withPage(pageNumber);
            var categoryList = List.of(category);
            var categoryDtoList = List.of(categoryDto);

            when(categoryRepository.findAll(eq(pageable)))
                    .thenReturn(new PageImpl<>(categoryList));
            when(categoryMapper.toCategoryInfoDtoList(categoryList))
                    .thenReturn(categoryDtoList);

            var result = categoryService.getCategories(pageSize, pageNumber);

            assertThat(result).isEqualTo(categoryDtoList);
            verify(categoryRepository).findAll(eq(pageable));
            verify(categoryMapper).toCategoryInfoDtoList(categoryList);
        }

        @ParameterizedTest
        @MethodSource("provideFailedPageParams")
        void getCategoriesShouldWorkWithoutPageParams(Integer pageSize, Integer pageNumber) {
            var categoryDto = mock(CategoryInfoDto.class);
            var category = mock(Category.class);
            var pageable = Pageable.ofSize(BusinessConstants.DEFAULT_PAGE_SIZE)
                    .withPage(BusinessConstants.DEFAULT_PAGE_NUMBER);
            var categoryList = List.of(category);
            var categoryDtoList = List.of(categoryDto);

            when(categoryRepository.findAll(eq(pageable)))
                    .thenReturn(new PageImpl<>(categoryList));
            when(categoryMapper.toCategoryInfoDtoList(categoryList))
                    .thenReturn(categoryDtoList);

            var result = categoryService.getCategories(pageSize, pageNumber);

            assertThat(result).isEqualTo(categoryDtoList);
            verify(categoryRepository).findAll(eq(pageable));
            verify(categoryMapper).toCategoryInfoDtoList(categoryList);
        }

        private static Stream<Arguments> provideFailedPageParams(){
            return Stream.of(
                    Arguments.of(null , null),
                    Arguments.of(-2, -2)
            );
        }
    }

    @Nested
    class updateCategory {
        @Test
        void updateCategoryShouldWorkWithoutParent() {
            var category = mock(Category.class);
            var categoryInfo = mock(CategoryInfoDto.class);
            Long id = 1L;

            when(categoryRepository.findById(id))
                    .thenReturn(Optional.ofNullable(category));
            when(categoryMapper.toCategoryInfo(category))
                    .thenReturn(categoryInfo);

            var result = categoryService.updateCategory(
                    createDtoWithoutParent, id
            );

            assertThat(result).isEqualTo(categoryInfo);

            verify(category).setName(createDtoWithoutParent.name());
            verify(categoryRepository).findById(id);
            verify(categoryMapper).toCategoryInfo(category);
        }

        @Test
        void updateCategoryShouldWork() {
            var category = mock(Category.class);
            var categoryInfo = mock(CategoryInfoDto.class);
            Long id = 1L;

            when(categoryRepository.findById(id))
                    .thenReturn(Optional.ofNullable(category));

            when(categoryMapper.toCategoryInfo(category))
                    .thenReturn(categoryInfo);

            var result = categoryService.updateCategory(
                    createDtoWithParent, id
            );

            assertThat(result).isEqualTo(categoryInfo);

            verify(category).setName(createDtoWithParent.name());
            verify(category).setParent(category);

            verify(categoryRepository, times(2)).findById(id);
            verify(categoryMapper).toCategoryInfo(category);
        }

        @Test
        void shouldThrowEntityNotFoundException(){
            var category = mock(Category.class);
            Long id = 1L;

            when(categoryRepository.findById(id))
                    .thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
            () -> categoryService.updateCategory(createDtoWithParent, id));

            verify(categoryRepository).findById(id);
            verify(category, never()).setName(createDtoWithParent.name());
            verify(category, never()).setParent(category);
        }
    }

    @Nested
    class deleteEmptyCategory {
        @Test
        void deleteEmptyCategoryShouldWork() {
            Long id = 1L;
            var category = mock(Category.class);
            when(categoryRepository.findById(id))
                    .thenReturn(Optional.ofNullable(category));

            categoryService.deleteEmptyCategory(id);

            verify(categoryRepository).findById(id);
            verify(categoryRepository).deleteById(id);
        }

        @ParameterizedTest
        @MethodSource("provideCategoryLists")
        void deleteEmptyCategoryWhenFilled(List<Product> products, List<Category> children) {
            Long id = 1L;
            var category = mock(Category.class);

            when(categoryRepository.findById(id))
                    .thenReturn(Optional.of(category));
            if (!products.isEmpty()) {
                when(category.getProducts()).thenReturn(products);
            } else if (!children.isEmpty()) {
                when(category.getChildren()).thenReturn(children);
            }

            assertThrows(
                    CategoryNotEmptyException.class,
                    () -> categoryService.deleteEmptyCategory(id)
            );

            verify(categoryRepository).findById(id);
            verify(categoryRepository, never()).deleteById(id);
        }
        private static Stream<Arguments> provideCategoryLists(){
            return Stream.of(
                    Arguments.of(List.of(new Product()), List.of(new Category())),
                    Arguments.of(List.of(new Product()), List.of()),
                    Arguments.of(List.of(), List.of(new Category()))
            );
        }
    }

    @Nested
    class deleteCategoryWithProducts {
        @Test
        void deleteCategoryWithProductsShouldWork() {
            Long id = 1L;
            var category = mock(Category.class);
            when(categoryRepository.findById(id))
                    .thenReturn(Optional.ofNullable(category));

            categoryService.deleteCategoryWithProducts(id);

            verify(categoryRepository).findById(id);
            verify(categoryRepository).deleteById(id);
        }

        @Test
        void deleteCategoryWithProductsShouldThrowException() {
            Long id = 1L;
            var category = mock(Category.class);
            when(categoryRepository.findById(id))
                    .thenReturn(Optional.empty());

            assertThrows(
                    EntityNotFoundException.class,
                    () -> categoryService.deleteCategoryWithProducts(id)
            );
            verify(categoryRepository).findById(id);
            verify(categoryRepository, never()).deleteById(id);
        }
    }
}