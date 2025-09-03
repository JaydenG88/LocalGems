package com.localgems.localgems_backend.service;
import org.springframework.stereotype.Service;
import com.localgems.localgems_backend.model.Business;
import com.localgems.localgems_backend.repository.BusinessRepository;
import com.localgems.localgems_backend.mapper.BusinessMapper;
import com.localgems.localgems_backend.dto.BusinessRequestDTO;
import com.localgems.localgems_backend.dto.BusinessResponseDTO;
import java.lang.StackWalker.Option;
import java.util.*;

@Service
public class BusinessService {
    
    private final BusinessRepository businessRepository;
    private final BusinessMapper businessMapper;

    public BusinessService(BusinessRepository businessRepository, BusinessMapper businessMapper) {
        this.businessRepository = businessRepository;
        this.businessMapper = businessMapper;
    }

    public BusinessResponseDTO createBusiness(BusinessRequestDTO businessRequestDTO) {
        Business business = businessMapper.dtoToEntity(businessRequestDTO);
        Business savedBusiness = businessRepository.save(business);
        return businessMapper.entityToDto(savedBusiness);
    }

    public List<BusinessResponseDTO> getAllBusinesses() {
        List<Business> businesses = businessRepository.findAll();
        List<BusinessResponseDTO> businessResponseDTOs = new ArrayList<>();
        for (Business business : businesses) {
            businessResponseDTOs.add(businessMapper.entityToDto(business));
        }
        return businessResponseDTOs;
    }

    public Optional<BusinessResponseDTO> getBusinessById(Long id) {
        Optional<Business> businessOpt = businessRepository.findById(id);
        return businessOpt.map(business -> businessMapper.entityToDto(business));
    }

    public Optional<BusinessResponseDTO> updateBusiness(Long id, BusinessRequestDTO businessRequestDTO) {
        return businessRepository.findById(id).map(existingBusiness -> {
            Business updatedBusiness = businessMapper.dtoToEntity(businessRequestDTO);
            updatedBusiness.setBusinessId(existingBusiness.getBusinessId());
            Business savedBusiness = businessRepository.save(updatedBusiness);
            return businessMapper.entityToDto(savedBusiness);
        });
    }

    public boolean deleteBusiness(Long id) {
        if (businessRepository.existsById(id)) {
            businessRepository.deleteById(id);
            return true;
        }
        return false;
    }





}
