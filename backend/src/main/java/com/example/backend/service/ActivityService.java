package com.example.backend.service;

import com.example.backend.model.Activity;
import org.springframework.data.util.Lock;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

public interface ActivityService {
    Long createActivityByUser(Timestamp start);

    Activity endActivity(Timestamp end, Long activityId);

    //    List<Activity> getActivitiesByUserId(User user);
    Activity getActivityById(Long id);
}
