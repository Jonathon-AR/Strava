package com.example.backend.controller;

import com.example.backend.model.Activity;
import com.example.backend.service.ActivityService;
import com.example.backend.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.backend.dto.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    Helper helper = new Helper();

    @PostMapping("/start")
    public ResponseEntity<?> startActivity(@RequestBody ActivityStartRequest requestBody) {
        try {
            Long activityId = activityService.createActivityByUser(new Timestamp(requestBody.getStart()));
            return ResponseEntity.ok().body(activityId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while starting the activity.");
        }
    }

    @PostMapping("/end")
    public ResponseEntity<?> endActivity(@RequestBody ActivityEndRequest requestBody) {
        try {
            Activity activity = activityService.endActivity(Timestamp.valueOf(requestBody.getEnd()), requestBody.getActivityId());
            return ResponseEntity.ok().body(activity);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while ending the activity.");
        }
    }

    @GetMapping("/{activityId}")
    public ResponseEntity<?> getActivity(@PathVariable Long activityId) {
        try {
            Activity activity = activityService.getActivityById(activityId);
            return ResponseEntity.ok().body(activity);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching the activity.");
        }
    }

    @GetMapping("/byUserId")
    public ResponseEntity<?> getActivitybyUserId() {
        try {
            List<Activity> list = activityService.getActivitiesByUserId();
            return ResponseEntity.ok().body(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching the activities.");
        }
    }
}

