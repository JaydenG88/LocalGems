package com.localgems.localgems_backend.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.mapstruct.MappingTarget;
import com.localgems.localgems_backend.model.Category;
import com.localgems.localgems_backend.dto.requestDTO.CategoryRequestDTO;
import com.localgems.localgems_backend.dto.responseDTO.CategoryResponseDTO;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    
    public static final CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    Category dtoToEntity(CategoryRequestDTO dto);

    CategoryResponseDTO entityToDto(Category business);

    void updateEntityFromDto(CategoryRequestDTO dto, @MappingTarget Category entity);
}
