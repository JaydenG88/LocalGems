package com.localgems.localgems_backend.dto.responseDTO;
import java.util.List;
import java.time.LocalDateTime;

public class BusinessResponseDTO {
    private Long businessId;
    private String googlePlaceId;
    private String name;
    private String address;
    private Long cityId;
    private String cityName; 
    private List<CategoryResponseDTO> categories;
    private Double latitude;
    private Double longitude;
    private String description;
    private LocalDateTime dateAdded;
    private String website;
    private Double averageRating;
    private Integer reviewCount;

    // Getters and Setters
    public Long getBusinessId() {
        return businessId;
    }
    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }
    public String getGooglePlaceId() {
        return googlePlaceId;
    }
    public void setGooglePlaceId(String googlePlaceId) {
        this.googlePlaceId = googlePlaceId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getCityName() {
        return cityName;
    }
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
    public Long getCityId() {
        return cityId;
    }
    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }
    public void setCategories(List<CategoryResponseDTO> categories) {
        this.categories = categories;
    }
    public List<CategoryResponseDTO> getCategories() {
        return categories;
    }
    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    public Double getLongitude() {
        return longitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public LocalDateTime getDateAdded() {
        return dateAdded;
    }
    public void setDateAdded(LocalDateTime dateAdded) {
        this.dateAdded = dateAdded;
    }
    public String getWebsite() {
        return website;
    }
    public void setWebsite(String website) {
        this.website = website;
    }
    public Double getAverageRating() {
        return averageRating;
    }
    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }
    
}
