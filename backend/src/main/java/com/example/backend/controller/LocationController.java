package com.example.backend.controller;

import com.example.backend.model.GpsPoint;
import com.example.backend.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gps")
@CrossOrigin(origins = "http://localhost:3000")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @PostMapping("/")
    public ResponseEntity<?> updateLocation(@RequestBody UpdateLocationRequest requestBody) {
        try {
            Map<String, BigDecimal> stats = (Map<String, BigDecimal>) locationService.updateLocation(requestBody.getGpsPointsList(), requestBody.getActivityId());
            return ResponseEntity.ok().body(stats);
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the activity.");
        }
    }

    @GetMapping("/{activityId}")
    public ResponseEntity<?> getLocationHistory(@PathVariable Long activityId) {
        try {
            Map<String, Object> stats = locationService.getLocationHistory(activityId);
            return ResponseEntity.ok().body(stats);
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching the location history.");
        }
    }
}

class UpdateLocationRequest {
    private List<GpsPoint> gpsPointsList;
    private Long activityId;

    public void setGpsPointsList(List<GpsPoint> gpsPointsList) {
        this.gpsPointsList = gpsPointsList;
    }

    public List<GpsPoint> getGpsPointsList() {
        return gpsPointsList;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }
}
