package com.example.backend.controller;

import com.example.backend.model.Activity;
import com.example.backend.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;

@RestController
@RequestMapping("/activity")
public class ActivityController {
    @Autowired
    private ActivityService activityService;

    @PostMapping("/start")
    public ResponseEntity<?> startActivity(@RequestBody ActivityStartRequest requestBody) {
        try {
            Long activityId = activityService.createActivityByUser(Timestamp.valueOf(requestBody.getStart()));
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while starting the activity.");
        }
    }

    //
    //    //gps points (activity id,  arr[xpoint, ypoint], - response (avg time, maxspeed, distance)
    //    @PostMapping("/gps")
    //    public ResponseEntity<?> locationUpdate(@RequestBody reqbody){
    //
    //    }


    @GetMapping("/id")
    public ResponseEntity<?> endActivity(@RequestBody ActivityDetailsRequest requestBody) {
        try {
            Activity activity = activityService.getActivityById(requestBody.getActivityId());
            return ResponseEntity.ok().body(activity);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while starting the activity.");
        }
    }
}

class ActivityStartRequest {
    private String start;

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }
}

class ActivityEndRequest {
    private String end;
    private Long activityId;

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }
}

//class gpsPoints

class ActivityDetailsRequest {
    private Long activityId;

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }
}