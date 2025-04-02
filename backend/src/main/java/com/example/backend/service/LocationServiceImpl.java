package com.example.backend.service;

import com.example.backend.model.Activity;
import com.example.backend.model.GpsPoint;
import com.example.backend.repository.ActivityRepository;
import com.example.backend.repository.GpsPointRepository;
import com.example.backend.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

@Service
public class LocationServiceImpl implements LocationService {

    private final ActivityRepository activityRepository;
    private final GpsPointRepository gpsPointRepository;
    private final Helper helper;

    @Autowired
    public LocationServiceImpl(ActivityRepository activityRepository, GpsPointRepository gpsPointRepository, Helper helper) {
        this.activityRepository = activityRepository;
        this.gpsPointRepository = gpsPointRepository;
        this.helper = helper;
    }

    @Override
    public Map<String, BigDecimal> updateLocation(List<GpsPoint> newGpsPoints, Long activityId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("No user is authenticated");
        }
        gpsPointRepository.saveAll(newGpsPoints);

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        // Incrementally update cumulative distance based on new GPS points
        BigDecimal updatedDistance = helper.updateCumulativeDistance(activity, newGpsPoints);
        activity.setDistance(updatedDistance);
        activity.setDistance(updatedDistance);

        newGpsPoints.sort(Comparator.comparing(GpsPoint::getTimestamp));
        GpsPoint latestNewPoint = newGpsPoints.get(newGpsPoints.size() - 1);
        activity.setLastGpsPoint(latestNewPoint);

        // Retrieve only the last five GPS points for speed calculation
        List<GpsPoint> lastFivePoints = gpsPointRepository.findTop5ByActivityIdOrderByTimestampDesc(activityId);
        if (lastFivePoints.size() < 5) {
            activityRepository.save(activity);
            return Collections.emptyMap();
        }
        // Reverse for ascending order
        Collections.reverse(lastFivePoints);

        // Calculate maximum segment speed over the last five points
        double maxSegmentSpeed = 0.0;
        for (int i = 1; i < lastFivePoints.size(); i++) {
            double segmentSpeed = helper.calculateSegmentSpeed(lastFivePoints.get(i - 1), lastFivePoints.get(i));
            if (segmentSpeed > maxSegmentSpeed) {
                maxSegmentSpeed = segmentSpeed;
            }
        }

        // Update maxSpeed if the newly computed segment speed is greater
        BigDecimal newSegmentSpeed = BigDecimal.valueOf(maxSegmentSpeed);
        if (activity.getMaxSpeed() == null || newSegmentSpeed.compareTo(activity.getMaxSpeed()) > 0) {
            activity.setMaxSpeed(newSegmentSpeed);
        }

        // Calculate cumulative average speed using the elapsed time since activity start
        Timestamp activityStart = activity.getStartTime();
        Timestamp lastTimestamp = lastFivePoints.get(lastFivePoints.size() - 1).getTimestamp();
        long elapsedSeconds = (lastTimestamp.getTime() - activityStart.getTime()) / 1000;
        double cumulativeAvgSpeed = (elapsedSeconds > 0)
                ? updatedDistance.doubleValue() / (elapsedSeconds / 3600.0) : 0.0;

        activityRepository.save(activity);

        Map<String, BigDecimal> stats = new HashMap<>();
        stats.put("maxSpeed", activity.getMaxSpeed());
        stats.put("avgSpeed", BigDecimal.valueOf(cumulativeAvgSpeed));
        stats.put("distance", activity.getDistance());

        return stats;
    }
}