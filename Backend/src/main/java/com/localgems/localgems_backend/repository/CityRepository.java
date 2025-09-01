package com.localgems.localgems_backend.repository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.localgems.localgems_backend.model.City;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

} 