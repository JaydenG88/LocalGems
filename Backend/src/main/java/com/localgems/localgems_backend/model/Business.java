package com.localgems.localgems_backend.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "businesses")
public class Business {
        @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long businessId;

    @Column(nullable = false, unique = true, name = "google_place_id")
    private String googlePlaceId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @ManyToMany
    @JoinTable(
        name = "business_categories",
        joinColumns = @JoinColumn(name = "business_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
        )    
    private List<Category> categories = new ArrayList<>();

    private Double latitude;
    private Double longitude;
    private String description;

    @Column(name = "date_added", nullable = false)
    private LocalDateTime dateAdded;

    @PrePersist
        protected void onCreate() {
        this.dateAdded = LocalDateTime.now();
    }
    
    private String website;

    @Column(name = "image_url")
    private String imageUrl;

    // Getters and Setters
    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
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

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
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

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


}
