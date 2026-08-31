package com.springboot.online_store.services;

import com.springboot.online_store.constants.BusinessConstants;
import com.springboot.online_store.dtos.category.CategoryCreateDto;
import com.springboot.online_store.dtos.category.CategoryInfoDto;
import com.springboot.online_store.entities.Category;
import com.springboot.online_store.exceptions.custom.CategoryNotEmptyException;
import com.springboot.online_store.mappers.CategoryMapper;
import com.springboot.online_store.repositories.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper mapper) {
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    public CategoryInfoDto createCategory(CategoryCreateDto categoryDto) {
        Category parentCategory = null;

        if(categoryDto.parentId() != null) {
            parentCategory = categoryRepository.getReferenceById(categoryDto.parentId());
        }

        Category category = new Category(categoryDto.name(), parentCategory);
        categoryRepository.save(category);

        return mapper.toCategoryInfo(category);
    }

    public List<CategoryInfoDto> getCategories(Integer pageSize, Integer pageNumber) {
        if(pageSize == null){
            pageSize = BusinessConstants.DEFAULT_PAGE_SIZE;
        }
        if(pageNumber == null){
            pageNumber = BusinessConstants.DEFAULT_PAGE_NUMBER;
        }

        Pageable pageable = Pageable.ofSize(pageSize).withPage(pageNumber);
        var categories = categoryRepository.findAll(pageable)
                .stream().toList();

        return mapper.toCategoryInfoDtoList(categories);
    }

    @Transactional
    public CategoryInfoDto updateCategory(CategoryCreateDto createDto, Long id) {
        var category = categoryRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        if(createDto.parentId() != null) {
            var parentCategory = categoryRepository.findById(id).orElseThrow(EntityNotFoundException::new);
            category.setParent(parentCategory);
        }

        category.setName(createDto.name());


        return mapper.toCategoryInfo(category);
    }

    public void deleteEmptyCategory(Long id) {
        var category = categoryRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        if(!category.getProducts().isEmpty() || !category.getChildren().isEmpty()){
            throw new CategoryNotEmptyException("this category contains products or subcategories");
        }
        categoryRepository.deleteById(id);
    }

    public void deleteCategoryWithProducts(Long id){
        var category = categoryRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        categoryRepository.deleteById(id);
    }
}
