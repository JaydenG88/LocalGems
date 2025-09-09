package com.localgems.localgems_backend.dto.updateDTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ReviewUpdateDTO {
    @Min(1)
    @Max(5)
    private int rating;

    private String description;

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

}
