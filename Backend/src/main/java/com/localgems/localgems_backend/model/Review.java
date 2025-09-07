package com.localgems.localgems_backend.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="review_id")
    private long reviewId;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false, length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public long getReviewId() { 
        return this.reviewId; 
    }
    public void setReviewId(long reviewId) { 
        this.reviewId = reviewId; 
    }

    public int getRating() { 
        return this.rating; 
    }
    public void setRating(int rating) { 
        this.rating = rating; 
    }

    public String getDescription() { 
        return this.description; 
    }
    public void setDescription(String description) {
         this.description = description; 
        }

    public User getUser() { 
        return user; 
    }
    public void setUser(User user) {
         this.user = user; 
        }

    public Business getBusiness() { 
        return business;
     }
    public void setBusiness(Business business) {
         this.business = business; 
        }

    public LocalDateTime getCreatedAt() {
        return createdAt; 
    }
    public LocalDateTime getUpdatedAt() { 
        return updatedAt; 
    }
}
