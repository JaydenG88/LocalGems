package com.localgems.localgems_backend.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.localgems.localgems_backend.model.Review;
import com.localgems.localgems_backend.dto.requestDTO.ReviewRequestDTO;
import com.localgems.localgems_backend.dto.responseDTO.ReviewResponseDTO;
import com.localgems.localgems_backend.dto.updateDTO.ReviewUpdateDTO;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    
    public static final ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);

    Review dtoToEntity(ReviewRequestDTO dto);

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "business.name", target = "businessName")
    @Mapping(source = "business.businessId", target = "businessId")
    ReviewResponseDTO entityToDto(Review review);
    
    void updateEntityFromDto(ReviewUpdateDTO dto, @MappingTarget Review entity);
} 