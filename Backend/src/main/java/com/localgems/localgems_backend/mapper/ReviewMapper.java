package com.localgems.localgems_backend.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.mapstruct.MappingTarget;
import com.localgems.localgems_backend.model.Review;
import com.localgems.localgems_backend.dto.ReviewRequestDTO;
import com.localgems.localgems_backend.dto.ReviewResponseDTO;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    
    public static final ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);

    Review dtoToEntity(ReviewRequestDTO dto);

    ReviewResponseDTO entityToDto(Review review);
    
    void updateEntityFromDto(ReviewRequestDTO dto, @MappingTarget Review entity);
} 