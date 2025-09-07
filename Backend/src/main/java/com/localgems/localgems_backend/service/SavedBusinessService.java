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

    public SavedBusinessResponseDTO createSavedBusiness(SavedBusinessRequestDTO dto) {
        SavedBusiness savedBusiness = savedBusinessMapper.dtoToEntity(dto);

        if(dto.getBusinessId() != null) {
            Business business = businessRepository.findById(dto.getBusinessId())
            .orElseThrow(() -> new NoSuchElementException("Business not found"));
            savedBusiness.setBusiness(business);
        }

        if(dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
            .orElseThrow(() -> new NoSuchElementException("User not found"));
            savedBusiness.setUser(user);
        }
        SavedBusiness saved = savedBusinessRepository.save(savedBusiness);
        return savedBusinessMapper.entityToDto(saved);
    }

    public List<SavedBusinessResponseDTO> getAllSavedBusinesses() {
        List<SavedBusiness> savedBusinesses = savedBusinessRepository.findAll();
        List<SavedBusinessResponseDTO> savedBusinessResponseDTOs = new ArrayList<>();

        for (SavedBusiness savedBusiness : savedBusinesses) {
            savedBusinessResponseDTOs.add(savedBusinessMapper.entityToDto(savedBusiness));
        }

        return savedBusinessResponseDTOs;
    }   

    public Optional<SavedBusinessResponseDTO> getSavedBusinessById(Long id) {
        Optional<SavedBusiness> savedBusinessOpt = savedBusinessRepository.findById(id);
        return savedBusinessOpt.map(savedBusiness -> savedBusinessMapper.entityToDto(savedBusiness));
    }

    public SavedBusinessResponseDTO updateSavedBusiness(Long id, SavedBusinessRequestDTO dto) {
        SavedBusiness savedBusiness = savedBusinessRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("SavedBusiness not found with id: " + id));
        
        if (dto.getBusinessId() != null) {
            Business business = businessRepository.findById(dto.getBusinessId())
            .orElseThrow(() -> new NoSuchElementException("Business not found"));
            savedBusiness.setBusiness(business);
        }

        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
            .orElseThrow(() -> new NoSuchElementException("User not found"));   
            savedBusiness.setUser(user);
        }
        savedBusinessMapper.updateEntityFromDto(dto, savedBusiness);
        SavedBusiness updatedSavedBusiness = savedBusinessRepository.save(savedBusiness);
        return savedBusinessMapper.entityToDto(updatedSavedBusiness);
    }

    public void deleteSavedBusiness(Long id) {
        if (!savedBusinessRepository.existsById(id)) {
            throw new NoSuchElementException("SavedBusiness not found with id: " + id);
        }
        savedBusinessRepository.deleteById(id);
    }


}
