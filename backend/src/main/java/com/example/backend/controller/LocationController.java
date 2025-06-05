package com.example.backend.controller;

import com.example.backend.dto.UpdateLocationRequest;
import com.example.backend.dto.LocationStatsResponse;
import com.example.backend.dto.LocationHistoryResponse;
import com.example.backend.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gps")
@CrossOrigin(origins = "http://localhost:3000")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @PostMapping("/")
    public ResponseEntity<?> updateLocation(@RequestBody UpdateLocationRequest requestBody) {
        try {
            LocationStatsResponse stats = locationService.updateLocation(requestBody.getGpsPointsList(), requestBody.getActivityId());
            return ResponseEntity.ok().body(stats);
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the activity.");
        }
    }

    @GetMapping("/{activityId}")
    public ResponseEntity<?> getLocationHistory(@PathVariable Long activityId) {
        try {
            LocationHistoryResponse stats = locationService.getLocationHistory(activityId);
            return ResponseEntity.ok().body(stats);
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching the location history.");
        }
    }
}
