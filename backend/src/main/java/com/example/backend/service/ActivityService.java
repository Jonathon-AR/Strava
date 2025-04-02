package com.example.backend.service;

import com.example.backend.model.Activity;
import com.example.backend.model.GpsPoint;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface ActivityService {
    Long createActivityByUser(Timestamp start);

    Activity endActivity(Timestamp end, Long activityId);

    Activity getActivityById(Long id);
}
