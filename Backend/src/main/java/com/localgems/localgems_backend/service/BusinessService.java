package com.localgems.localgems_backend.service;
import org.springframework.stereotype.Service;
import com.localgems.localgems_backend.model.Business;
import com.localgems.localgems_backend.repository.BusinessRepository;
import com.localgems.localgems_backend.model.City;
import com.localgems.localgems_backend.repository.CityRepository;
import com.localgems.localgems_backend.model.Category;
import com.localgems.localgems_backend.repository.CategoryRepository;
import com.localgems.localgems_backend.mapper.BusinessMapper;
import com.localgems.localgems_backend.dto.externalDTO.GooglePlacesDTO;
import com.localgems.localgems_backend.dto.requestDTO.BusinessRequestDTO;
import com.localgems.localgems_backend.dto.responseDTO.BusinessResponseDTO;
import com.localgems.localgems_backend.dto.responseDTO.CityResponseDTO;
import com.localgems.localgems_backend.repository.ReviewRepository;
import com.localgems.localgems_backend.model.Review;
import com.localgems.localgems_backend.service.external.GoogleMapsService;
import com.localgems.localgems_backend.service.external.OpenAIService;

import java.util.*;

@Service
public class BusinessService {
    
    private final BusinessRepository businessRepository;
    private final BusinessMapper businessMapper;
    private final CityRepository cityRepository;
    private final CityService cityService;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;
    private final GoogleMapsService googleMapsService;
    private final OpenAIService openAIService;

    public BusinessService(
            BusinessRepository businessRepository, 
            BusinessMapper businessMapper, 
            CityRepository cityRepository, 
            CityService cityService,
            CategoryRepository categoryRepository, 
            ReviewRepository reviewRepository, 
            GoogleMapsService googleMapsService,
            OpenAIService openAIService) {
        this.businessRepository = businessRepository;
        this.businessMapper = businessMapper;
        this.cityRepository = cityRepository;
        this.cityService = cityService;
        this.categoryRepository = categoryRepository;
        this.reviewRepository = reviewRepository;
        this.googleMapsService = googleMapsService;
        this.openAIService = openAIService;
    }

    public BusinessResponseDTO submitBusiness(String businessName, String address, String city, String state) {
        String search = businessName + ", " + address + ", " + city + ", " + state;
        GooglePlacesDTO placesDto = googleMapsService.getPlaceDetailsBySearch(search);
        if (placesDto == null) {
            throw new NoSuchElementException("Business not found in Google Places");
        }
        
        // Check if the city exists in our database, if not, add it
        if (placesDto.getCity() != null && placesDto.getState() != null) {
            try {
                // Try to find or create the city
                CityResponseDTO cityDto = cityService.getOrCreateCity(placesDto.getCity(), placesDto.getState());
                System.out.println("City found or created: " + cityDto.getName() + ", " + cityDto.getState() + 
                                  " (ID: " + cityDto.getCityId() + ")");
            } catch (Exception e) {
                System.err.println("Warning: Could not process city: " + e.getMessage());
            }
        }

         BusinessRequestDTO businessRequestDTO = openAIService.generateBusinessRequestDTO(placesDto);
            if (businessRequestDTO != null) {
                return createBusiness(businessRequestDTO);
            } else {
                throw new RuntimeException("Failed to generate BusinessRequestDTO from OpenAI");
            }
    }

    public BusinessResponseDTO createBusiness(BusinessRequestDTO businessRequestDTO) {
        Business business = businessMapper.dtoToEntity(businessRequestDTO);
        
        // Handle city association - both with explicit cityId and through GooglePlacesDTO city information
        if (businessRequestDTO.getCityId() != null) {
            // Try to find the city by ID first
            try {
                City city = cityRepository.findById(businessRequestDTO.getCityId())
                    .orElseThrow(() -> new NoSuchElementException("City not found with id: " + businessRequestDTO.getCityId()));
                business.setCity(city);
            } catch (NoSuchElementException e) {
                // If city ID doesn't exist but we have city name and state from a GooglePlacesDTO,
                // try to create the city dynamically
                if (businessRequestDTO.getCityName() != null && businessRequestDTO.getCityState() != null) {
                    CityResponseDTO cityResponseDTO = cityService.getOrCreateCity(
                        businessRequestDTO.getCityName(), 
                        businessRequestDTO.getCityState()
                    );
                    
                    City city = cityRepository.findById(cityResponseDTO.getCityId())
                        .orElseThrow(() -> new RuntimeException("Failed to retrieve newly created city"));
                    
                    business.setCity(city);
                } else {
                    throw e; // Re-throw if we don't have city name and state
                }
            }
        } else if (businessRequestDTO.getCityName() != null && businessRequestDTO.getCityState() != null) {
            // If no city ID is provided but we have name and state, get or create the city
            CityResponseDTO cityResponseDTO = cityService.getOrCreateCity(
                businessRequestDTO.getCityName(), 
                businessRequestDTO.getCityState()
            );
            
            City city = cityRepository.findById(cityResponseDTO.getCityId())
                .orElseThrow(() -> new RuntimeException("Failed to retrieve newly created city"));
            
            business.setCity(city);
        }

        // Handle category associations
        if (businessRequestDTO.getCategoryIds() != null) {
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
        // Get initial list of businesses filtered by city and state
        List<Business> filteredBusinesses = businessRepository.filterBusinesses(city, state, null, null);
        List<BusinessResponseDTO> filteredResponseDTOs = new ArrayList<>();
        
        // Apply additional filters in memory (categories and rating)
        for (Business business : filteredBusinesses) {
            boolean includeByCategory = categories == null || categories.isEmpty();
            boolean includeByRating = true;
            
            // Check categories if needed
            if (!includeByCategory && categories != null && !categories.isEmpty()) {
                for (Category category : business.getCategories()) {
                    if (categories.contains(category)) {
                        includeByCategory = true;
                        break;
                    }
                }
            }
            
            // Check rating if needed
            if (rating != null) {
                List<Review> reviews = reviewRepository.findByBusiness_BusinessId(business.getBusinessId());
                if (!reviews.isEmpty()) {
                    double avgRating = reviews.stream()
                        .mapToInt(Review::getRating)
                        .average()
                        .orElse(0.0);
                    includeByRating = avgRating >= rating;
                } else {
                    includeByRating = false; // No reviews means it doesn't meet the rating criteria
                }
            }
            
            // Add to results if it passes all filters
            if (includeByCategory && includeByRating) {
                BusinessResponseDTO dto = businessMapper.entityToDto(business);
                
                // Add rating information
                Double averageRating = reviewRepository.findAverageRatingByBusinessId(business.getBusinessId());
                Integer reviewCount = reviewRepository.findByBusiness_BusinessId(business.getBusinessId()).size();
                dto.setAverageRating(averageRating);
                dto.setReviewCount(reviewCount);
                
                filteredResponseDTOs.add(dto);
            }
        }
        
        return filteredResponseDTOs;
    }

    public BusinessResponseDTO updateBusiness(Long id, BusinessRequestDTO dto) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Business not found with id: " + id));
        businessMapper.updateEntityFromDto(dto, business);

        // Handle city association with lazy loading
        if (dto.getCityId() != null) {
            try {
                City city = cityRepository.findById(dto.getCityId())
                        .orElseThrow(() -> new NoSuchElementException("City not found with id: " + dto.getCityId()));
                business.setCity(city);
            } catch (NoSuchElementException e) {
                // If city ID doesn't exist but we have city name and state
                if (dto.getCityName() != null && dto.getCityState() != null) {
                    CityResponseDTO cityResponseDTO = cityService.getOrCreateCity(
                        dto.getCityName(), 
                        dto.getCityState()
                    );
                    
                    City city = cityRepository.findById(cityResponseDTO.getCityId())
                        .orElseThrow(() -> new RuntimeException("Failed to retrieve newly created city"));
                    
                    business.setCity(city);
                } else {
                    throw e; // Re-throw if we don't have city name and state
                }
            }
        } else if (dto.getCityName() != null && dto.getCityState() != null) {
            // If no city ID is provided but we have name and state
            CityResponseDTO cityResponseDTO = cityService.getOrCreateCity(
                dto.getCityName(), 
                dto.getCityState()
            );
            
            City city = cityRepository.findById(cityResponseDTO.getCityId())
                .orElseThrow(() -> new RuntimeException("Failed to retrieve newly created city"));
            
            business.setCity(city);
        }

        // Handle category associations
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
