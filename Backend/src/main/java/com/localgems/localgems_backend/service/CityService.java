package com.localgems.localgems_backend.service;
import org.springframework.stereotype.Service;
import com.localgems.localgems_backend.model.City;
import com.localgems.localgems_backend.dto.CityRequestDTO;
import com.localgems.localgems_backend.dto.CityResponseDTO;
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
}
