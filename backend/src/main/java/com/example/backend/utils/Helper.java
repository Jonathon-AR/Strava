package com.example.backend.utils;

import com.example.backend.model.Activity;
import com.example.backend.model.GpsPoint;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Helper {

    public double calculateSegmentSpeed(GpsPoint p1, GpsPoint p2) {
        double distanceKm = calculateDistance(p1, p2);
        long timeDiffSec = (p2.getTimestamp().getTime() - p1.getTimestamp().getTime()) / 1000;
        if (timeDiffSec <= 0) {
            return 0.0;
        }
        return distanceKm / (timeDiffSec / 3600.0);
    }

    private double calculateDistance(GpsPoint p1, GpsPoint p2) {
        final int EARTH_RADIUS_KM = 6371;
        double lat1 = p1.getLatitude().doubleValue();
        double lon1 = p1.getLongitude().doubleValue();
        double lat2 = p2.getLatitude().doubleValue();
        double lon2 = p2.getLongitude().doubleValue();

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    public BigDecimal updateCumulativeDistance(Activity activity, List<GpsPoint> newPoints) {
        BigDecimal previousDistance = activity.getDistance() != null
                ? activity.getDistance() : BigDecimal.ZERO;
        double incrementalDistance = 0.0;

        GpsPoint lastProcessed = activity.getLastGpsPoint();
        newPoints.sort(Comparator.comparing(GpsPoint::getTimestamp));

        List<GpsPoint> pointsToProcess;
        if (lastProcessed != null) {
            // Process only points after the last processed point
            pointsToProcess = newPoints.stream()
                    .filter(p -> p.getTimestamp().after(lastProcessed.getTimestamp()))
                    .collect(Collectors.toList());
            if (!pointsToProcess.isEmpty()) {
                // Add distance from last processed point to the first new point
                incrementalDistance += calculateDistance(lastProcessed, pointsToProcess.get(0));
            }
        } else {
            pointsToProcess = newPoints;
        }
        for (int i = 1; i < pointsToProcess.size(); i++) {
            incrementalDistance += calculateDistance(pointsToProcess.get(i - 1), pointsToProcess.get(i));
        }

        return previousDistance.add(BigDecimal.valueOf(incrementalDistance));
    }
}
