
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
import com.localgems.localgems_backend.service.CityService;
import com.localgems.localgems_backend.dto.CityRequestDTO;
import com.localgems.localgems_backend.dto.CityResponseDTO;
import java.util.*;

@RestController
@RequestMapping("/api/cities")
public class CityController {
	private final CityService cityService;

	public CityController(CityService cityService) {
		this.cityService = cityService;
	}

	@GetMapping
	public ResponseEntity<List<CityResponseDTO>> getAllCities() {
		List<CityResponseDTO> cities = cityService.getAllCities();
		return new ResponseEntity<>(cities, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<CityResponseDTO> getCityById(@PathVariable Long id) {
		Optional<CityResponseDTO> cityOpt = cityService.getCityById(id);
		return cityOpt.map(city -> new ResponseEntity<>(city, HttpStatus.OK))
					  .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@PostMapping
	public ResponseEntity<CityResponseDTO> createCity(@RequestBody CityRequestDTO cityRequestDTO) {
		CityResponseDTO createdCity = cityService.createCity(cityRequestDTO);
		return new ResponseEntity<>(createdCity, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<CityResponseDTO> updateCity(@PathVariable Long id, @RequestBody CityRequestDTO cityRequestDTO) {
		try {
			CityResponseDTO updatedCity = cityService.updateCity(id, cityRequestDTO);
			return new ResponseEntity<>(updatedCity, HttpStatus.OK);
		} catch (NoSuchElementException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCity(@PathVariable Long id) {
		try {
			cityService.deleteCity(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (NoSuchElementException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
