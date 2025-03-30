package com.example.backend.service;

import com.example.backend.model.Activity;

import java.sql.Timestamp;
import java.util.List;

public interface ActivityService {
    Long createActivityByUser(String token, Timestamp start);
    Activity createActivity(Activity activity);
    List<Activity> getAllActivities();
//    List<Activity> getActivitiesByUserId(User user);
    Activity getActivityById(Long id);
    void deleteActivity(Long id);
}
