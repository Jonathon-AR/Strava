package com.example.backend.service;

import com.example.backend.model.GpsPoint;
import com.example.backend.dto.LocationStatsResponse;
import com.example.backend.dto.LocationHistoryResponse;
import java.util.List;

public interface LocationService {
    LocationStatsResponse updateLocation(List<GpsPoint> gpsPointsList, Long activityId);
    LocationHistoryResponse getLocationHistory(Long activityId);
}
