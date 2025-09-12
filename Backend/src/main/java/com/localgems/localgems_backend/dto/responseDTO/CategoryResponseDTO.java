package com.localgems.localgems_backend.dto.responseDTO;

public class CategoryResponseDTO {
    private String name;
    private Long categoryId;

    // Getters and Setters
    public String getName() {
        return name;
    }
    public Long getCategoryId() {
        return categoryId;
    } 
    public void setName(String name) {
        this.name = name;
    }
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
