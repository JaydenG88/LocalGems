package com.localgems.localgems_backend.service;
import org.springframework.stereotype.Service;
import com.localgems.localgems_backend.model.Review;
import com.localgems.localgems_backend.model.Business;
import com.localgems.localgems_backend.model.User;
import com.localgems.localgems_backend.repository.ReviewRepository;
import com.localgems.localgems_backend.repository.BusinessRepository;
import com.localgems.localgems_backend.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import com.localgems.localgems_backend.mapper.ReviewMapper;
import com.localgems.localgems_backend.dto.requestDTO.ReviewRequestDTO;
import com.localgems.localgems_backend.dto.responseDTO.ReviewResponseDTO;
import com.localgems.localgems_backend.dto.updateDTO.ReviewUpdateDTO;

import java.util.*;

@Service
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;

    public ReviewService (ReviewRepository reviewRepository, ReviewMapper reviewMapper, BusinessRepository businessRepository, UserRepository userRepository) {
        
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;

    }

    public ReviewResponseDTO createReview(ReviewRequestDTO reviewRequestDTO) {
        Review review = reviewMapper.dtoToEntity(reviewRequestDTO);

        if(reviewRequestDTO.getBusinessId() != null) {
            Business business = businessRepository.findById(reviewRequestDTO.getBusinessId())
            .orElseThrow(() -> new EntityNotFoundException("Business not found"));
            review.setBusiness(business);
        }

        if(reviewRequestDTO.getUserId() != null) {
            User user = userRepository.findById(reviewRequestDTO.getUserId())
            .orElseThrow(() -> new EntityNotFoundException("user not found"));
            review.setUser(user);
        }

        Review savedReview = reviewRepository.save(review);
        return reviewMapper.entityToDto(savedReview);
    }


    public List<ReviewResponseDTO> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        List<ReviewResponseDTO> reviewResponseDTOs = new ArrayList<>();

        for (Review review : reviews) {
            reviewResponseDTOs.add(reviewMapper.entityToDto(review));
        }

        return reviewResponseDTOs;
    }

    public Optional<ReviewResponseDTO> getReviewById(Long id) {
        Optional<Review> reviewOpt = reviewRepository.findById(id);
        return reviewOpt.map( review -> reviewMapper.entityToDto(review));
    }

    public List<ReviewResponseDTO> getReviewsByBusinessId(Long businessId) {
        List<Review> reviews = reviewRepository.findByBusiness_BusinessId(businessId);
        List<ReviewResponseDTO> reviewResponseDTOs = new ArrayList<>();

        for (Review review : reviews) {
            reviewResponseDTOs.add(reviewMapper.entityToDto(review));
        }

        return reviewResponseDTOs;
    }

    public List<ReviewResponseDTO> getReviewsByUserId(Long userId) {
        List<Review> reviews = reviewRepository.findByUser_UserId(userId);
        List<ReviewResponseDTO> reviewResponseDTOs = new ArrayList<>();

        for (Review review : reviews) {
            reviewResponseDTOs.add(reviewMapper.entityToDto(review));
        }

        return reviewResponseDTOs;
    }

    public ReviewResponseDTO updateReview(Long id, ReviewUpdateDTO reviewUpdateDTO) {
        Review review = reviewRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Review not found with id: " + id));
        reviewMapper.updateEntityFromDto(reviewUpdateDTO, review);

        Review updatedReview = reviewRepository.save(review);
        return reviewMapper.entityToDto(updatedReview);
        
    }

    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new NoSuchElementException("Review not found with id: " + id);
        }
        reviewRepository.deleteById(id);
    }

}
