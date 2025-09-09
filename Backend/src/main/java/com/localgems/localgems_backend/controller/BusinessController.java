package com.localgems.localgems_backend.controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.localgems.localgems_backend.service.BusinessService;
import com.localgems.localgems_backend.dto.BusinessRequestDTO;
import com.localgems.localgems_backend.dto.BusinessResponseDTO;
import java.util.*;

@RestController
@RequestMapping("/api/businesses")
public class BusinessController {
    private final BusinessService businessService;

    public BusinessController(BusinessService businessService) {
        this.businessService = businessService;
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
