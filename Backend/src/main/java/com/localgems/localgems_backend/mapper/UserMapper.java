package com.localgems.localgems_backend.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.mapstruct.MappingTarget;
import com.localgems.localgems_backend.model.User;
import com.localgems.localgems_backend.dto.requestDTO.UserRequestDTO;
import com.localgems.localgems_backend.dto.responseDTO.UserResponseDTO;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    public static final UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User dtoToEntity(UserRequestDTO dto);

    UserResponseDTO entityToDto(User user);
    
    void updateEntityFromDto(UserRequestDTO dto, @MappingTarget User entity);

}
