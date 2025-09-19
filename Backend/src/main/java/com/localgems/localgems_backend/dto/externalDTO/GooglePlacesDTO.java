package com.localgems.localgems_backend.dto.externalDTO;
import java.util.List;

public class GooglePlacesDTO {
    private String placeId;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String city;
    private String state;
    private List<String> categories;
    private Double rating;
    private Integer totalRatings;
    private String priceLevel;
    private String editorialSummary; 
    private List<String> reviewSnippets;

    // Getters and Setters

    public String getPlaceId() {
        return placeId;
    }
    public void setPlaceId(String placeId) {
        this.placeId = placeId;
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
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public List<String> getCategories() {
        return categories;
    }
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
    public Double getRating() {
        return rating;
    }
    public void setRating(Double rating) {
        this.rating = rating;
    }
    public Integer getTotalRatings() {
        return totalRatings;
    }
    public void setTotalRatings(Integer totalRatings) {
        this.totalRatings = totalRatings;
    }
    public String getPriceLevel() {
        return priceLevel;
    }
    public void setPriceLevel(String priceLevel) {
        this.priceLevel = priceLevel;
    }
    public String getEditorialSummary() {
        return editorialSummary;
    }
    public void setEditorialSummary(String editorialSummary) {
        this.editorialSummary = editorialSummary;
    }
    public List<String> getReviewSnippets() {
        return reviewSnippets;
    }
    public void setReviewSnippets(List<String> reviewSnippets) {
        this.reviewSnippets = reviewSnippets;
    }
    
}

