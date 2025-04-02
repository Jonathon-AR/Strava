package com.example.backend.service;

import com.example.backend.model.GpsPoint;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface LocationService {
    Map<String, BigDecimal> updateLocation(List<GpsPoint> gpsPointsList, Long activityId);
}
