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

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        for (GpsPoint gpsPoint : newGpsPoints) {
            gpsPoint.setActivity(activity);
        }
        gpsPointRepository.saveAll(newGpsPoints);

        Map<String, BigDecimal> stats = new HashMap<>();

        BigDecimal maxSegmentSpeed = BigDecimal.ZERO;
        for (GpsPoint gpsPoint : newGpsPoints) {
            if (maxSegmentSpeed.compareTo(gpsPoint.getSpeed()) < 0) {
                maxSegmentSpeed = gpsPoint.getSpeed();
            }
        }

        if (activity.getMaxSpeed() == null || maxSegmentSpeed.compareTo(activity.getMaxSpeed()) > 0) {
            activity.setMaxSpeed(maxSegmentSpeed);
        }

        stats.put("maxSpeed", activity.getMaxSpeed());

        BigDecimal updatedDistance = helper.updateCumulativeDistance(activity, newGpsPoints);
        activity.setDistance(updatedDistance);

        newGpsPoints.sort(Comparator.comparing(GpsPoint::getTimestamp));
        GpsPoint latestNewPoint = newGpsPoints.get(newGpsPoints.size() - 1);
        activity.setLastGpsPoint(latestNewPoint);

        stats.put("distance", activity.getDistance());
        activityRepository.save(activity);

        if (gpsPointRepository.countByActivityId(activityId) > 4) {
            Timestamp activityStart = activity.getStartTime();
            Timestamp lastTimestamp = gpsPointRepository.findTopByActivityIdOrderByTimestampDesc(activityId).getTimestamp();
            long elapsedSeconds = (lastTimestamp.getTime() - activityStart.getTime()) / 1000;
            double cumulativeAvgSpeed = (elapsedSeconds > 0)
                    ? updatedDistance.doubleValue() / (elapsedSeconds / 3600.0)
                    : 0.0;

            stats.put("avgSpeed", BigDecimal.valueOf(cumulativeAvgSpeed));
        }

        return stats;
    }


    public Map<String, Object> getLocationHistory(Long activityId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("No user is authenticated");
        }
        Map<String, Object> map = new HashMap<>();
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));
        map.put("start_time", activity.getStartTime());
        map.put("end_time", activity.getEndTime());
        map.put("distance", activity.getDistance());
        map.put("max_speed", activity.getMaxSpeed());
        List<GpsPoint> gpsPointList = gpsPointRepository.findByActivityIdOrderByTimestampAsc(activityId);
        map.put("gps_points", gpsPointList);
        return map;
    }
}