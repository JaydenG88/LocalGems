package com.localgems.localgems_backend.service;
import org.springframework.stereotype.Service;
import com.localgems.localgems_backend.model.Business;
import com.localgems.localgems_backend.repository.BusinessRepository;
import com.localgems.localgems_backend.model.City;
import com.localgems.localgems_backend.repository.CityRepository;
import com.localgems.localgems_backend.model.Category;
import com.localgems.localgems_backend.repository.CategoryRepository;
import com.localgems.localgems_backend.mapper.BusinessMapper;
import com.localgems.localgems_backend.dto.requestDTO.BusinessRequestDTO;
import com.localgems.localgems_backend.dto.responseDTO.BusinessResponseDTO;
import com.localgems.localgems_backend.repository.ReviewRepository;
import com.localgems.localgems_backend.model.Review;
import com.localgems.localgems_backend.service.ReviewService;

import java.util.*;

@Service
public class BusinessService {
    
    private final BusinessRepository businessRepository;
    private final BusinessMapper businessMapper;
    private final CityRepository cityRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;

    public BusinessService(BusinessRepository businessRepository, BusinessMapper businessMapper, CityRepository cityRepository, CategoryRepository categoryRepository, ReviewRepository reviewRepository) {
        this.businessRepository = businessRepository;
        this.businessMapper = businessMapper;
        this.cityRepository = cityRepository;
        this.categoryRepository = categoryRepository;
        this.reviewRepository = reviewRepository;
    }

    public BusinessResponseDTO createBusiness(BusinessRequestDTO businessRequestDTO) {
        Business business = businessMapper.dtoToEntity(businessRequestDTO);
        if(businessRequestDTO.getCityId() != null) {
            City city = cityRepository.findById(businessRequestDTO.getCityId())
                    .orElseThrow(() -> new NoSuchElementException("City not found with id: " + businessRequestDTO.getCityId()));
            business.setCity(city);
        }

        if(businessRequestDTO.getCategoryIds() != null) {
            List<Category> categories = categoryRepository.findAllById(businessRequestDTO.getCategoryIds());
            business.setCategories(categories);
        }

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
        Double averageRating = reviewRepository.findAverageRatingByBusinessId(id);
        System.out.println("Average Rating: " + averageRating);
        Integer reviewCount = reviewRepository.findByBusiness_BusinessId(id).size();

        businessOpt.ifPresent(business -> {
            BusinessResponseDTO dto = businessMapper.entityToDto(business);
            dto.setAverageRating(averageRating);
            dto.setReviewCount(reviewCount);
        });

        BusinessResponseDTO dto = businessOpt.map(business -> businessMapper.entityToDto(business)).orElse(null);
        if (dto != null) {
            dto.setAverageRating(averageRating);
            dto.setReviewCount(reviewCount);
        }

        return dto == null ? Optional.empty() : Optional.of(dto);
    }

    public List<BusinessResponseDTO> filterBusinesses(City city, String state, List<Category> categories, Double rating) {
        List<Business> filteredBusinesses = businessRepository.filterBusinesses(city, state, categories, rating);
        List<BusinessResponseDTO> filteredResponseDTOs = new ArrayList<>();

        for (Business business : filteredBusinesses) {
            filteredResponseDTOs.add(businessMapper.entityToDto(business));
       }

       return filteredResponseDTOs;

    }

    public BusinessResponseDTO updateBusiness(Long id, BusinessRequestDTO dto) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Business not found with id: " + id));
        businessMapper.updateEntityFromDto(dto, business);

        if(dto.getCityId() != null) {
            City city = cityRepository.findById(dto.getCityId())
                    .orElseThrow(() -> new NoSuchElementException("City not found with id: " + dto.getCityId()));
            business.setCity(city);
        }

        if(dto.getCategoryIds() != null) {
            List<Category> categories = categoryRepository.findAllById(dto.getCategoryIds());
            business.setCategories(categories);
        }

        Business updatedBusiness = businessRepository.save(business);
        return businessMapper.entityToDto(updatedBusiness);
    }

    public void deleteBusiness(Long id) {
        if (businessRepository.existsById(id)) {
            businessRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("Business not found with id: " + id);
        }
    }





}
