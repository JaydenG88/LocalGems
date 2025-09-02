package com.localgems.localgems_backend.dto;

import java.time.LocalDateTime;

public class SavedBusinessResponseDTO {
    private Long savedBusinessId;
    private Long userId;
    private Long businessId;
    private LocalDateTime savedAt; 

    // Getters and Setters
    public Long getSavedBusinessId() {
        return savedBusinessId;
    }
    public void setSavedBusinessId(Long savedBusinessId) {
        this.savedBusinessId = savedBusinessId;
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
    public LocalDateTime getSavedAt() {
        return savedAt;
    }
    public void setSavedAt(LocalDateTime savedAt) {
        this.savedAt = savedAt;
    }
    
}
