package com.localgems.localgems_backend.dto;
import java.util.List;

public class BusinessRequestDTO {
    private String googlePlaceId;
    private String name;
    private String address;
    private Long cityId; 
    private List<Long> categoryIds; 
    private Double latitude;
    private Double longitude;
    private String description;
    private String website;
    private String imageUrl;

    // Getters and Setters
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
    public Long getCityId() {
        return cityId;
    }
    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }
    public List<Long> getCategoryIds() {
        return categoryIds;
    }
    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
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
    public String getWebsite() {
        return website;
    }
    public void setWebsite(String website) {
        this.website = website;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
