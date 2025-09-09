package com.localgems.localgems_backend.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.localgems.localgems_backend.model.SavedBusiness;
import com.localgems.localgems_backend.dto.requestDTO.SavedBusinessRequestDTO;
import com.localgems.localgems_backend.dto.responseDTO.SavedBusinessResponseDTO;

@Mapper(componentModel = "spring")
public interface SavedBusinessMapper {
    
    public static final SavedBusinessMapper INSTANCE = Mappers.getMapper(SavedBusinessMapper.class);
    SavedBusiness dtoToEntity(SavedBusinessRequestDTO dto);

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "business.businessId", target = "businessId")
    SavedBusinessResponseDTO entityToDto(SavedBusiness savedBusiness);

    void updateEntityFromDto(SavedBusinessRequestDTO dto, @MappingTarget SavedBusiness entity);
}
