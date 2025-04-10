package com.example.backend.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "activities")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "start_time", nullable = false)
    private Timestamp startTime;

    @Column(name = "end_time")
    private Timestamp endTime;

    @Column(name = "distance", precision = 7, scale = 2)
    private BigDecimal distance;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @OneToOne
    @JoinColumn(name = "last_gps_point")
    private GpsPoint lastGpsPoint;

    @Column(name = "max_speed", precision = 5, scale = 2)
    private BigDecimal maxSpeed;

    public enum Status {
        ACTIVE, COMPLETED
    }

    public Activity() {
    }

    public Activity(Long userId, Timestamp startTime, Timestamp endTime, BigDecimal distance, Status status) {
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.distance = distance;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public GpsPoint getLastGpsPoint() {
        return this.lastGpsPoint;
    }

    public void setLastGpsPoint(GpsPoint lastGpsPoint) {
        this.lastGpsPoint = lastGpsPoint;
    }
}
