package com.localgems.localgems_backend.model;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "cities", 
    uniqueConstraints = @UniqueConstraint(columnNames = {"name", "state"})
)
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "city_id")
    private Long cityId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String state;  

    @OneToMany(mappedBy = "city")
    private List<Business> businesses = new ArrayList<>();

    // Getters and Setters
    public Long getCityId() {
        return cityId;
    }
    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<Business> getBusinesses() {
        return businesses;
    }
    public void setBusinesses(List<Business> businesses) {
        this.businesses = businesses;
    }


}
