package com.example.backend.repository;

import com.example.backend.model.GpsPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GpsPointRepository extends JpaRepository<GpsPoint, Long> {
    List<GpsPoint> findTop5ByActivityIdOrderByTimestampDesc(Long activityId);
}
