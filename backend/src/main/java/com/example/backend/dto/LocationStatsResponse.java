package com.example.backend.dto;

import java.math.BigDecimal;

public class LocationStatsResponse {
    private BigDecimal maxSpeed;
    private BigDecimal distance;
    private BigDecimal avgSpeed;

    public BigDecimal getMaxSpeed() {
        return maxSpeed;
    }
    public void setMaxSpeed(BigDecimal maxSpeed) {
        this.maxSpeed = maxSpeed;
    }
    public BigDecimal getDistance() {
        return distance;
    }
    public void setDistance(BigDecimal distance) {
        this.distance = distance;
    }
    public BigDecimal getAvgSpeed() {
        return avgSpeed;
    }
    public void setAvgSpeed(BigDecimal avgSpeed) {
        this.avgSpeed = avgSpeed;
    }
}