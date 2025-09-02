package com.localgems.localgems_backend.dto;

import java.time.LocalDateTime;

public class SavedBusinessRequestDTO {
    private Long userId;
    private Long businessId;

    // Getters and Setters
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
