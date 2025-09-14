package com.localgems.localgems_backend.controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.localgems.localgems_backend.service.BusinessService;
import com.localgems.localgems_backend.dto.requestDTO.BusinessRequestDTO;
import com.localgems.localgems_backend.dto.responseDTO.BusinessResponseDTO;
import com.localgems.localgems_backend.model.City;
import com.localgems.localgems_backend.model.Category;
import com.localgems.localgems_backend.repository.CityRepository;
import com.localgems.localgems_backend.repository.CategoryRepository;


import java.util.*;

@RestController
@RequestMapping("/api/businesses")
public class BusinessController {
    private final BusinessService businessService;
    private final CityRepository cityRepository;
    private final CategoryRepository categoryRepository;

    public BusinessController(BusinessService businessService, CityRepository cityRepository, CategoryRepository categoryRepository) {
        this.businessService = businessService;
        this.cityRepository = cityRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public ResponseEntity<List<BusinessResponseDTO>> getAllBusinesses() {
        List<BusinessResponseDTO> businesses = businessService.getAllBusinesses();
        return new ResponseEntity<>(businesses, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusinessResponseDTO> getBusinessById(@PathVariable Long id) {
        Optional<BusinessResponseDTO> businessOpt = businessService.getBusinessById(id);
        return businessOpt.map(business -> new ResponseEntity<>(business, HttpStatus.OK))
                          .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    @GetMapping("/filter")
    public ResponseEntity<List<BusinessResponseDTO>> filterBusinesses (
        @RequestParam(required = false) String state,
        @RequestParam(required = false) Long cityId,
        @RequestParam(required = false) Double rating,
        @RequestParam(required = false) List<Long> categoryIds
    ) {
        try {
            // Handle city parameter
            City cityEntity = null;
            if (cityId != null) {
                try {
                    cityEntity = cityRepository.findById(cityId)
                        .orElse(null); // Use null instead of throwing an exception
                } catch (Exception e) {
                    // Just log the error and continue with null
                    System.out.println("Error finding city with ID " + cityId + ": " + e.getMessage());
                }
            }

            // Handle categories parameter
            List<Category> categoryEntities = new ArrayList<>();
            if (categoryIds != null && !categoryIds.isEmpty()) {
                for (Long categoryId : categoryIds) {
                    try {
                        categoryRepository.findById(categoryId)
                            .ifPresent(categoryEntities::add);
                    } catch (Exception e) {
                        // Just log the error and continue
                        System.out.println("Error finding category with ID " + categoryId + ": " + e.getMessage());
                    }
                }
            }

            // Call the service with potentially empty collections
            List<BusinessResponseDTO> filteredBusinessDTOs = businessService.filterBusinesses(
                cityEntity, 
                state, 
                categoryEntities.isEmpty() ? null : categoryEntities, 
                rating
            );
            
            return ResponseEntity.ok(filteredBusinessDTOs);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error filtering businesses: " + e.getMessage()
            );
        }
    }

    @PostMapping
    public ResponseEntity<BusinessResponseDTO> createBusiness(@RequestBody BusinessRequestDTO businessRequestDTO) {
        BusinessResponseDTO createdBusiness = businessService.createBusiness(businessRequestDTO);
        return new ResponseEntity<>(createdBusiness, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BusinessResponseDTO> updateBusiness(@PathVariable Long id, @RequestBody BusinessRequestDTO businessRequestDTO) {
        try {
            BusinessResponseDTO updatedBusiness = businessService.updateBusiness(id, businessRequestDTO);
            return new ResponseEntity<>(updatedBusiness, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBusiness(@PathVariable Long id) {
        try {
            businessService.deleteBusiness(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
