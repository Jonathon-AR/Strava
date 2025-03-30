package com.example.backend.controller;

import com.example.backend.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;

@RestController
@RequestMapping("/activity")
public class ActivityController {
    @Autowired
    private ActivityService activityService;

    @PostMapping("/start")
    public ResponseEntity<?> startActivity(@RequestBody ActivityStartRequest requestBody) {
        try {
            Long activityId=activityService.createActivityByUser(requestBody.getToken(), Timestamp.valueOf(requestBody.getStart()));
            return ResponseEntity.ok().body(activityId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while starting the activity.");
        }
    }

    //activity close (userid,null, ending_timestamp,__,'Completed') - response (Avg time, maxSpeed, distance, graph)


//    @PostMapping("/end")
//    public ResponseEntity<?> endActivity(@RequestBody reqbody){
//
//    }
//
//    //gps points (activity id,  arr[xpoint, ypoint], - response (avg time, maxspeed, distance)
//    @PostMapping("/gps")
//    public ResponseEntity<?> locationUpdate(@RequestBody reqbody){
//
//    }

}
class ActivityStartRequest {
    private String token;
    private String start;

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getStart() {
        return start;
    }
    public void setStart(String start) {
        this.start = start;
    }
}
