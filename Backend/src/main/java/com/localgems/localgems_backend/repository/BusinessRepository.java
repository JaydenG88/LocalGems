package com.localgems.localgems_backend.repository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.localgems.localgems_backend.model.Business;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {
    
}
