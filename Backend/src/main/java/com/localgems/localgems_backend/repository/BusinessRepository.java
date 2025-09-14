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

    // Using native query to avoid complex JPQL issues
    @Query(value = """
        SELECT DISTINCT b.* FROM businesses b
        JOIN cities c ON b.city_id = c.city_id
        LEFT JOIN (
            SELECT business_id, AVG(rating) as avg_rating 
            FROM reviews 
            GROUP BY business_id
        ) r ON b.business_id = r.business_id
        WHERE 
            (:city_id IS NULL OR b.city_id = :city_id) AND
            (:state IS NULL OR c.state = :state) AND
            (:rating IS NULL OR r.avg_rating >= :rating OR r.avg_rating IS NULL)
    """, nativeQuery = true)
    List<Business> filterBusinessesByBasicCriteria(
        @Param("city_id") Long cityId,
        @Param("state") String state,
        @Param("rating") Double rating
    );
    
    // Simpler query for filtering just by city and state
    @Query("""
        SELECT b FROM Business b
        JOIN b.city c
        WHERE (:city IS NULL OR c = :city)
        AND (:state IS NULL OR c.state = :state)
    """)
    List<Business> filterBusinesses(
        @Param("city") City city,
        @Param("state") String state,
        @Param("categories") List<Category> categories,
        @Param("rating") Double rating
    );
}

