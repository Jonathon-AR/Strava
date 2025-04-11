package com.example.backend.repository;

import com.example.backend.model.GpsPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GpsPointRepository extends JpaRepository<GpsPoint, Long> {
    GpsPoint findTopByActivityIdOrderByTimestampDesc(Long activityId);
    Long countByActivityId(Long activityId);
    List<GpsPoint> findByActivityIdOrderByTimestampAsc(Long activityId);
}
