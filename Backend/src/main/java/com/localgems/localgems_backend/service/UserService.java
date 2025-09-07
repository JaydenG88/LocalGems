package com.localgems.localgems_backend.service;
import org.springframework.stereotype.Service;
import com.localgems.localgems_backend.model.User;
import com.localgems.localgems_backend.repository.UserRepository;
import com.localgems.localgems_backend.mapper.UserMapper;
import com.localgems.localgems_backend.dto.UserRequestDTO;
import com.localgems.localgems_backend.dto.UserResponseDTO;
import java.util.*;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        User user = userMapper.dtoToEntity(userRequestDTO);
        User savedUser = userRepository.save(user);
        return userMapper.entityToDto(savedUser);
    }

    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponseDTO> userResponseDTOs = new ArrayList<>();
        for (User user : users) {
            userResponseDTOs.add(userMapper.entityToDto(user));
        }
        return userResponseDTOs;
    }

    public Optional<UserResponseDTO> getUserById(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        return userOpt.map(user -> userMapper.entityToDto(user));
    }

    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));
        userMapper.updateEntityFromDto(dto, user);
        User updatedUser = userRepository.save(user);
        return userMapper.entityToDto(updatedUser);
        
    }

    public void deleteUser(Long id) {
        if(userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("User not found with id: " + id);
        }
    }

}
