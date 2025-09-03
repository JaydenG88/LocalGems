package com.localgems.localgems_backend.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.localgems.localgems_backend.model.User;
import com.localgems.localgems_backend.dto.UserRequestDTO;
import com.localgems.localgems_backend.dto.UserResponseDTO;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    public static final UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User dtoToEntity(UserRequestDTO dto);

    UserResponseDTO entityToDto(User user);

}
