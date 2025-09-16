

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
import org.springframework.http.HttpStatusCode;

import com.localgems.localgems_backend.service.ReviewService;
import com.localgems.localgems_backend.dto.requestDTO.ReviewRequestDTO;
import com.localgems.localgems_backend.dto.responseDTO.ReviewResponseDTO;
import com.localgems.localgems_backend.dto.updateDTO.ReviewUpdateDTO;

import java.util.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
	private final ReviewService reviewService;

	public ReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	@GetMapping
	public ResponseEntity<List<ReviewResponseDTO>> getAllReviews() {
		List<ReviewResponseDTO> reviews = reviewService.getAllReviews();
		return new ResponseEntity<>(reviews, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ReviewResponseDTO> getReviewById(@PathVariable Long id) {
		Optional<ReviewResponseDTO> reviewOpt = reviewService.getReviewById(id);
		return reviewOpt.map(review -> new ResponseEntity<>(review, HttpStatus.OK))
						.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/business/{businessId}")
	public ResponseEntity<List<ReviewResponseDTO>> getReviewsByBusinessId(@PathVariable Long businessId) {
		List<ReviewResponseDTO> reviews = reviewService.getReviewsByBusinessId(businessId);
		if (reviews.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(reviews, HttpStatus.OK);
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<List<ReviewResponseDTO>> getReviewsByUserId(@PathVariable Long userId) {
		List<ReviewResponseDTO> reviews = reviewService.getReviewsByUserId(userId);
		if(reviews.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(reviews, HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<ReviewResponseDTO> createReview(@RequestBody ReviewRequestDTO reviewRequestDTO) {
		ReviewResponseDTO createdReview = reviewService.createReview(reviewRequestDTO);
		return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ReviewResponseDTO> updateReview(@PathVariable Long id, @RequestBody ReviewUpdateDTO reviewUpdateDTO) {
		try {
			ReviewResponseDTO updatedReview = reviewService.updateReview(id, reviewUpdateDTO);
			return new ResponseEntity<>(updatedReview, HttpStatus.OK);
		} catch (NoSuchElementException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
		try {
			reviewService.deleteReview(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (NoSuchElementException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
