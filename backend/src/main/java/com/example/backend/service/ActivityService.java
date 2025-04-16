package com.example.backend.service;

import com.example.backend.model.Activity;
import java.sql.Timestamp;
import java.util.List;

public interface ActivityService {
    Long createActivityByUser(Timestamp start);

    Activity endActivity(Timestamp end, Long activityId);

    Activity getActivityById(Long id);

    List<Activity> getActivitiesByUserId();
}
