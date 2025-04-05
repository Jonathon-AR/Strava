package com.example.backend.service;

import com.example.backend.dto.LocationHistoryResponse;
import com.example.backend.dto.LocationStatsResponse;
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
    public LocationStatsResponse updateLocation(List<GpsPoint> newGpsPoints, Long activityId) {
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

        BigDecimal maxSegmentSpeed = BigDecimal.ZERO;
        for (GpsPoint gpsPoint : newGpsPoints) {
            if (maxSegmentSpeed.compareTo(gpsPoint.getSpeed()) < 0) {
                maxSegmentSpeed = gpsPoint.getSpeed();
            }
        }

        if (activity.getMaxSpeed() == null || maxSegmentSpeed.compareTo(activity.getMaxSpeed()) > 0) {
            activity.setMaxSpeed(maxSegmentSpeed);
        }

        BigDecimal updatedDistance = helper.updateCumulativeDistance(activity, newGpsPoints);
        activity.setDistance(updatedDistance);

        newGpsPoints.sort(Comparator.comparing(GpsPoint::getTimestamp));
        GpsPoint latestNewPoint = newGpsPoints.get(newGpsPoints.size() - 1);
        activity.setLastGpsPoint(latestNewPoint);
        activityRepository.save(activity);

        LocationStatsResponse statsResponse = new LocationStatsResponse();
        statsResponse.setMaxSpeed(activity.getMaxSpeed());
        statsResponse.setDistance(activity.getDistance());

        if (gpsPointRepository.countByActivityId(activityId) > 4) {
            Timestamp activityStart = activity.getStartTime();
            Timestamp lastTimestamp = gpsPointRepository.findTopByActivityIdOrderByTimestampDesc(activityId).getTimestamp();
            long elapsedSeconds = (lastTimestamp.getTime() - activityStart.getTime()) / 1000;
            double cumulativeAvgSpeed = (elapsedSeconds > 0)
                    ? updatedDistance.doubleValue() / (elapsedSeconds / 3600.0)
                    : 0.0;
            statsResponse.setAvgSpeed(BigDecimal.valueOf(cumulativeAvgSpeed));
        } else {
            statsResponse.setAvgSpeed(BigDecimal.ZERO);
        }

        return statsResponse;
    }

    @Override
    public LocationHistoryResponse getLocationHistory(Long activityId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("No user is authenticated");
        }
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));
        LocationHistoryResponse response = new LocationHistoryResponse();
        response.setStartTime(activity.getStartTime());
        response.setEndTime(activity.getEndTime());
        response.setDistance(activity.getDistance());
        response.setMaxSpeed(activity.getMaxSpeed());
        List<GpsPoint> gpsPointList = gpsPointRepository.findByActivityIdOrderByTimestampAsc(activityId);
        response.setGpsPoints(gpsPointList);
        return response;
    }
}