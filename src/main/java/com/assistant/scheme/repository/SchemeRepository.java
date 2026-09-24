package com.assistant.scheme.repository;

import com.assistant.scheme.model.Scheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SchemeRepository extends JpaRepository<Scheme, Long> {

    @Query("SELECT s FROM Scheme s WHERE " +
           "(:name IS NULL OR s.name LIKE %:name%) AND " +
           "(:category IS NULL OR s.category = :category) AND " +
           "(:state IS NULL OR s.state = :state OR s.state = 'Central') AND " +
           "(:department IS NULL OR s.department LIKE %:department%)")
    List<Scheme> searchSchemes(
            @Param("name") String name,
            @Param("category") String category,
            @Param("state") String state,
            @Param("department") String department
    );

    List<Scheme> findByCategory(String category);
    
    List<Scheme> findByState(String state);
}
