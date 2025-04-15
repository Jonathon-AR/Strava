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

@RestController
@RequestMapping("/activity")
@CrossOrigin(origins = "http://localhost:3000") // Enable CORS for this method
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    Helper helper=new Helper();

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

    @GetMapping("/id")
    public ResponseEntity<?> getActivity(@RequestBody ActivityDetailsRequest requestBody) {
        try {
            Activity activity = activityService.getActivityById(requestBody.getActivityId());
            return ResponseEntity.ok().body(activity);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching the activity.");
        }
    }
}

