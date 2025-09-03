package com.localgems.localgems_backend.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.localgems.localgems_backend.model.City;
import com.localgems.localgems_backend.dto.CityRequestDTO;
import com.localgems.localgems_backend.dto.CityResponseDTO;

@Mapper(componentModel = "spring")
public interface CityMapper {
    
    public static final CityMapper INSTANCE = Mappers.getMapper(CityMapper.class);

    City dtoToEntity(CityRequestDTO dto);

    CityResponseDTO entityToDto(City city);
    
}
