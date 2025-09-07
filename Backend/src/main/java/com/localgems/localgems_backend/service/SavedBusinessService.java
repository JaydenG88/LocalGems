package com.localgems.localgems_backend.service;
import org.springframework.stereotype.Service;
import com.localgems.localgems_backend.model.SavedBusiness;
import com.localgems.localgems_backend.model.Business;
import com.localgems.localgems_backend.model.User;
import com.localgems.localgems_backend.repository.SavedBusinessRepository;
import com.localgems.localgems_backend.repository.BusinessRepository;
import com.localgems.localgems_backend.repository.UserRepository;
import com.localgems.localgems_backend.mapper.SavedBusinessMapper;
import com.localgems.localgems_backend.dto.SavedBusinessRequestDTO;
import com.localgems.localgems_backend.dto.SavedBusinessResponseDTO;
import java.util.*;

@Service
public class SavedBusinessService {
    private final SavedBusinessRepository savedBusinessRepository;
    private final SavedBusinessMapper savedBusinessMapper;
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;

    public SavedBusinessService(SavedBusinessRepository savedBusinessRepository, SavedBusinessMapper savedBusinessMapper, BusinessRepository businessRepository, UserRepository userRepository) {
        this.savedBusinessRepository = savedBusinessRepository;
        this.savedBusinessMapper = savedBusinessMapper;
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;
    }

    public List<SavedBusinessResponseDTO> getAllSavedBusinesses() {
        List<SavedBusiness> savedBusinesses = savedBusinessRepository.findAll();
        List<SavedBusinessResponseDTO> savedBusinessResponseDTOs = new ArrayList<>();

        for (SavedBusiness savedBusiness : savedBusinesses) {
            savedBusinessResponseDTOs.add(savedBusinessMapper.entityToDto(savedBusiness));
        }
        
        return savedBusinessResponseDTOs;
    }   
}
