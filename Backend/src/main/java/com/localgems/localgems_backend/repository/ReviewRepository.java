package com.localgems.localgems_backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.localgems.localgems_backend.model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBusiness_BusinessId(Long businessId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.business.id = :businessId")
    Double findAverageRatingByBusinessId(@Param("businessId") Long businessId);
} 