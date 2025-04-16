package com.example.backend.service;

import com.example.backend.model.Activity;
import com.example.backend.model.GpsPoint;
import com.example.backend.model.User;
import com.example.backend.repository.ActivityRepository;
import com.example.backend.repository.GpsPointRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

@Service
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final GpsPointRepository gpsPointRepository;
    private final Helper helper;

    @Autowired
    public ActivityServiceImpl(ActivityRepository activityRepository, UserRepository userRepository, GpsPointRepository gpsPointRepository, Helper helper) {
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
        this.gpsPointRepository = gpsPointRepository;
        this.helper = helper;
    }

    @Override
    public Activity getActivityById(Long id) {
        return activityRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Activity not found"));
    }

    @Override
    public Activity endActivity(Timestamp end, Long activityId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("No user is authenticated");
        }
        Activity activity = getActivityById(activityId);
        activity.setEndTime((end));
        activity.setStatus(Activity.Status.COMPLETED);
        activityRepository.save(activity);
        return activity;
    }

    @Override
    public Long createActivityByUser(Timestamp start) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("No user is authenticated");
        }
        String userEmail = authentication.getPrincipal().toString();
        User user = userRepository.findByEmail(userEmail);
        Activity activity = new Activity(user.getId(), start, null, BigDecimal.ZERO, Activity.Status.ACTIVE);
        activityRepository.save(activity);
        return activity.getId();
    }

    @Override
    public List<Activity> getActivitiesByUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("No user is authenticated");
        }
        String userEmail = authentication.getPrincipal().toString();
        User user = userRepository.findByEmail(userEmail);
        List<Activity> activities = activityRepository.findByUserId(user.getId());
        return activities;
    }
}