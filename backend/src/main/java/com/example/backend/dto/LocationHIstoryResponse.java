package com.example.backend.dto;
import com.example.backend.dto.GpsPoint;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class LocationHistoryResponse {
    private Timestamp startTime;
    private Timestamp endTime;
    private BigDecimal distance;
    private BigDecimal maxSpeed;
    private List<GpsPoint> gpsPoints;

    public Timestamp getStartTime() {
        return startTime;
    }
    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }
    public Timestamp getEndTime() {
        return endTime;
    }
    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }
    public BigDecimal getDistance() {
        return distance;
    }
    public void setDistance(BigDecimal distance) {
        this.distance = distance;
    }
    public BigDecimal getMaxSpeed() {
        return maxSpeed;
    }
    public void setMaxSpeed(BigDecimal maxSpeed) {
        this.maxSpeed = maxSpeed;
    }
    public List<GpsPoint> getGpsPoints() {
        return gpsPoints;
    }
    public void setGpsPoints(List<GpsPoint> gpsPoints) {
        this.gpsPoints = gpsPoints;
    }
}