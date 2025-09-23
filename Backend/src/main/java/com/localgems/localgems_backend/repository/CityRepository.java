package com.localgems.localgems_backend.repository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.localgems.localgems_backend.model.City;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    /**
     * Find a city by its name and state
     * @param name the city name
     * @param state the state abbreviation
     * @return the city if found
     */
    Optional<City> findByNameIgnoreCaseAndStateIgnoreCase(String name, String state);
} 