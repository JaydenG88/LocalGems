package com.localgems.localgems_backend.repository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.localgems.localgems_backend.model.SavedBusiness;

@Repository
public interface SavedBusinessRepository extends JpaRepository<SavedBusiness, Long> {

    
} 