package com.localgems.localgems_backend.repository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.localgems.localgems_backend.model.Business;
import com.localgems.localgems_backend.model.City;
import com.localgems.localgems_backend.model.Category;
import java.util.List;
import org.springframework.data.repository.query.Param;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {

    @Query("""
        SELECT b
        FROM Business b
        JOIN b.reviews r
        JOIN b.city c
        JOIN b.categories cat
        WHERE (:city IS NULL OR c = :city)
          AND (:state IS NULL OR c.state = :state)
          AND (:categories IS NULL OR cat = :categories)
        GROUP BY b
        HAVING (:rating IS NULL OR AVG(r.rating) >= :rating)
    """)
    List<Business> filterBusinesses(
        @Param("city") City city,
        @Param("state") String state,
        @Param("category") List<Category> categories,
        @Param("rating") Double rating
    );
}

