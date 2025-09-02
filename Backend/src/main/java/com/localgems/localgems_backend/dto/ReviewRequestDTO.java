package com.localgems.localgems_backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ReviewRequestDTO {
    @Min(1)
    @Max(5)
    private int rating;

    @NotBlank
    private String description;

    @NotNull
    private Long userId;

    @NotNull
    private Long businessId;

    // Getters and Setters
    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
    public String getDescription() {   
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getBusinessId() {
        return businessId;
    }
    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

}
