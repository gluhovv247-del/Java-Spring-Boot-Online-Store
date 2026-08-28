package com.springboot.online_store.mappers;

import com.springboot.online_store.dtos.category.CategoryInfoDto;
import com.springboot.online_store.entities.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {
    CategoryInfoDto toCategoryInfo(Category category);
    List<CategoryInfoDto> toCategoryInfoDtoList(List<Category> categories);
}
