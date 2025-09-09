

package com.localgems.localgems_backend.controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.localgems.localgems_backend.service.SavedBusinessService;
import com.localgems.localgems_backend.dto.SavedBusinessRequestDTO;
import com.localgems.localgems_backend.dto.SavedBusinessResponseDTO;
import java.util.*;

@RestController
public class SavedBusinessController {
	private final SavedBusinessService savedBusinessService;

	public SavedBusinessController(SavedBusinessService savedBusinessService) {
		this.savedBusinessService = savedBusinessService;
	}

	@GetMapping("/saved-businesses")
	public ResponseEntity<List<SavedBusinessResponseDTO>> getAllSavedBusinesses() {
		List<SavedBusinessResponseDTO> savedBusinesses = savedBusinessService.getAllSavedBusinesses();
		return new ResponseEntity<>(savedBusinesses, HttpStatus.OK);
	}

	@GetMapping("/saved-businesses/{id}")
	public ResponseEntity<SavedBusinessResponseDTO> getSavedBusinessById(@PathVariable Long id) {
		Optional<SavedBusinessResponseDTO> savedBusinessOpt = savedBusinessService.getSavedBusinessById(id);
		return savedBusinessOpt.map(savedBusiness -> new ResponseEntity<>(savedBusiness, HttpStatus.OK))
							  .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@PostMapping("/saved-businesses")
	public ResponseEntity<SavedBusinessResponseDTO> createSavedBusiness(@RequestBody SavedBusinessRequestDTO savedBusinessRequestDTO) {
		SavedBusinessResponseDTO createdSavedBusiness = savedBusinessService.createSavedBusiness(savedBusinessRequestDTO);
		return new ResponseEntity<>(createdSavedBusiness, HttpStatus.CREATED);
	}

	@PutMapping("/saved-businesses/{id}")
	public ResponseEntity<SavedBusinessResponseDTO> updateSavedBusiness(@PathVariable Long id, @RequestBody SavedBusinessRequestDTO savedBusinessRequestDTO) {
		try {
			SavedBusinessResponseDTO updatedSavedBusiness = savedBusinessService.updateSavedBusiness(id, savedBusinessRequestDTO);
			return new ResponseEntity<>(updatedSavedBusiness, HttpStatus.OK);
		} catch (NoSuchElementException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/saved-businesses/{id}")
	public ResponseEntity<Void> deleteSavedBusiness(@PathVariable Long id) {
		try {
			savedBusinessService.deleteSavedBusiness(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (NoSuchElementException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
