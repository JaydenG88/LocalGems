package com.localgems.localgems_backend.repository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.localgems.localgems_backend.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

} 