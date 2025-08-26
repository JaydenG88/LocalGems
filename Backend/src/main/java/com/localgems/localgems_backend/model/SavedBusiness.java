package com.localgems.localgems_backend.model;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="saved_businesses")
public class SavedBusiness {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="saved_business_id")
    private long savedBusinessId;

    @Column(name = "saved_at", nullable = false, updatable = false)
    private LocalDateTime savedAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
   
    @ManyToOne
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    // Getters and Setters
    public long getSavedBusinessId() {
        return this.savedBusinessId;
    }

    public void setSavedBusinessId(long savedBusinessId) {
        this.savedBusinessId = savedBusinessId;
    }

    public LocalDateTime getSavedAt() {
        return this.savedAt;
    }

    public void setSavedAt(LocalDateTime savedAt) {
        this.savedAt = savedAt;
    }

    public User getUser() {
        return this.user;
    }

}
