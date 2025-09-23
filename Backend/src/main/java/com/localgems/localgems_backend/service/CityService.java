package com.localgems.localgems_backend.service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.localgems.localgems_backend.model.City;
import com.localgems.localgems_backend.dto.requestDTO.CityRequestDTO;
import com.localgems.localgems_backend.dto.responseDTO.CityResponseDTO;
import com.localgems.localgems_backend.mapper.CityMapper;
import com.localgems.localgems_backend.repository.CityRepository;
import java.util.*;

@Service
public class CityService {
    private final CityRepository cityRepository;
    private final CityMapper cityMapper;

    public CityService(CityRepository cityRepository, CityMapper cityMapper) {
        this.cityRepository = cityRepository;
        this.cityMapper = cityMapper;

    }

    public CityResponseDTO createCity(CityRequestDTO cityRequestDTO) {
        City city = cityMapper.dtoToEntity(cityRequestDTO);
        City savedCity = cityRepository.save(city);
        return cityMapper.entityToDto(savedCity);
    }

    public List<CityResponseDTO> getAllCities() {
        List<City> cities = cityRepository.findAll();
        List<CityResponseDTO> cityResponseDTOs = new ArrayList<>();
        for (City city : cities) {
            cityResponseDTOs.add(cityMapper.entityToDto(city));
        }

        return cityResponseDTOs;
    }

    public Optional<CityResponseDTO> getCityById(Long id) {
        Optional<City> cityOpt = cityRepository.findById(id);
        return cityOpt.map(city -> cityMapper.entityToDto(city) );
    }

    public CityResponseDTO updateCity(Long id, CityRequestDTO cityRequestDTO) {
        City city = cityRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Category not found with id: " + id));
        cityMapper.updateEntityFromDto(cityRequestDTO, city);
        City updatedCity = cityRepository.save(city);
        return cityMapper.entityToDto(updatedCity);
        
    }

    public void deleteCity(Long id) {
        if (cityRepository.existsById(id)) {
            cityRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("City not found with id: " + id);
        }
    }
    
    /**
     * Get a city by name and state, creating it if it doesn't exist
     * This implements lazy loading of cities into the database
     * 
     * @param name the city name
     * @param state the state abbreviation
     * @return the city DTO
     */
    @Transactional
    public CityResponseDTO getOrCreateCity(String name, String state) {
        if (name == null || state == null) {
            throw new IllegalArgumentException("City name and state cannot be null");
        }
        
        // Normalize the inputs
        name = name.trim();
        state = state.trim().toUpperCase();
        
        // Validate state abbreviation (must be 2 characters)
        if (state.length() != 2) {
            throw new IllegalArgumentException("State must be a 2-character abbreviation");
        }
        
        // Check if city already exists
        Optional<City> existingCity = cityRepository.findByNameIgnoreCaseAndStateIgnoreCase(name, state);
        
        if (existingCity.isPresent()) {
            return cityMapper.entityToDto(existingCity.get());
        } else {
            // Create new city
            CityRequestDTO cityRequestDTO = new CityRequestDTO();
            cityRequestDTO.setName(name);
            cityRequestDTO.setState(state);
            
            City city = cityMapper.dtoToEntity(cityRequestDTO);
            City savedCity = cityRepository.save(city);
            
            System.out.println("Created new city: " + name + ", " + state + " with ID " + savedCity.getCityId());
            
            return cityMapper.entityToDto(savedCity);
        }
    }
}
