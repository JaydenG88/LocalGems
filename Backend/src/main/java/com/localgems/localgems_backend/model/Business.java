package com.localgems.localgems_backend.model;
import jakarta.persistence.*;

@Entity
@Table(name = "businesses")
public class Business {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long businessId;
    
}
