package com.example.backend.controller;

import com.example.backend.model.Activity;
import com.example.backend.service.ActivityService;
import com.example.backend.utils.Helper;
import com.example.backend.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/activity")
public class ActivityController {

    private final ActivityService activityService;
    private final Helper helper;

    @Autowired
    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
        this.helper = new Helper();
    }

    @PostMapping("/start")
    public ResponseEntity<Long> startActivity(@RequestBody ActivityStartRequest requestBody) {
        try {
            Long activityId = activityService.createActivityByUser(new Timestamp(requestBody.getStart()));
            return ResponseEntity.ok(activityId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PostMapping("/end")
    public ResponseEntity<Activity> endActivity(@RequestBody ActivityEndRequest requestBody) {
        try {
            Activity activity = activityService.endActivity(Timestamp.valueOf(requestBody.getEnd()), requestBody.getActivityId());
            return ResponseEntity.ok(activity);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/{activityId}")
    public ResponseEntity<Activity> getActivity(@PathVariable Long activityId) {
        try {
            Activity activity = activityService.getActivityById(activityId);
            return ResponseEntity.ok(activity);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/byUserId")
    public ResponseEntity<List<Activity>> getActivityByUserId() {
        try {
            List<Activity> list = activityService.getActivitiesByUserId();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}

