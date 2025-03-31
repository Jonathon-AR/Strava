package com.example.backend.service;

import com.example.backend.model.Activity;
import org.springframework.data.util.Lock;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

public interface ActivityService {
    Long createActivityByUser(Timestamp start);
    Activity endActivity(Timestamp end, Long activityId);
//    Activity createActivity(Activity activity);
//    List<Activity> getAllActivities();
//    List<Activity> getActivitiesByUserId(User user);
    Activity getActivityById(Long id);
//    void deleteActivity(Long id);
}
