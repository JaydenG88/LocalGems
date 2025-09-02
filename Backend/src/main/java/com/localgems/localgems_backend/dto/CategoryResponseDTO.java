package com.localgems.localgems_backend.dto;

public class CategoryResponseDTO {
    private Long categoryId;
    private String name;

    // Getters and Setters
    public Long getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
