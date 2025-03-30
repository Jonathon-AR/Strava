package com.example.backend.service;

import com.example.backend.model.Activity;
import com.example.backend.model.User;
import com.example.backend.repository.ActivityRepository;
import com.example.backend.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;

    @Autowired
    private Helper helper;

    @Autowired
    public ActivityServiceImpl(ActivityRepository activityRepository, Helper helper) {
        this.activityRepository = activityRepository;
    }

    @Override
    public Activity createActivity(Activity activity) {
        // Business logic, validation, processing before saving
        return activityRepository.save(activity);
    }

    @Override
    public List<Activity> getAllActivities() {
        return List.of();
    }

    @Override
    public Activity getActivityById(Long id) {
        return activityRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Activity not found"));
    }

    @Override
    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }
//    public List<Activity> getActivitiesByUser(User user) {
//        return activityRepository.findAll();
//    }

    public Long createActivityByUser(String token, Timestamp start){
        User user = helper.findUserByToken(token);
        Activity activity = new Activity(user.getId(), start, null, BigDecimal.ZERO, Activity.Status.ACTIVE);
        activityRepository.save(activity);
        return activity.getId();
    }
}