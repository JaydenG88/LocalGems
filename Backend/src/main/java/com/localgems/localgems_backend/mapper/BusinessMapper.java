package com.localgems.localgems_backend.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.mapstruct.MappingTarget;
import com.localgems.localgems_backend.model.Business;
import com.localgems.localgems_backend.dto.BusinessRequestDTO;
import com.localgems.localgems_backend.dto.BusinessResponseDTO;

@Mapper(componentModel = "spring")
public interface BusinessMapper {
    
    public static final BusinessMapper INSTANCE = Mappers.getMapper(BusinessMapper.class);

    Business dtoToEntity(BusinessRequestDTO dto);

    @Mapping(source = "city.name", target = "cityName")
    @Mapping(source = "city.cityId", target = "cityId")
    BusinessResponseDTO entityToDto(Business business);

    void updateEntityFromDto(BusinessRequestDTO dto, @MappingTarget Business entity);

}
