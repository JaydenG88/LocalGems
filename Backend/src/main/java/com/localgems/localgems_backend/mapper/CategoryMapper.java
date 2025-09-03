package com.localgems.localgems_backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.localgems.localgems_backend.model.Category;
import com.localgems.localgems_backend.dto.CategoryRequestDTO;
import com.localgems.localgems_backend.dto.CategoryResponseDTO;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    
    public static final CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    Category dtoToEntity(CategoryRequestDTO dto);

    CategoryResponseDTO entityToDto(Category business);

}
