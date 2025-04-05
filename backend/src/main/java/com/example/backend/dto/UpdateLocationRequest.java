package com.example.backend.dto;
import com.example.backend.dto.GpsPoint;
import java.util.List;

public class UpdateLocationRequest {
    private List<GpsPoint> gpsPointsList;
    private Long activityId;

    public List<GpsPoint> getGpsPointsList() {
        return gpsPointsList;
    }
    public void setGpsPointsList(List<GpsPoint> gpsPointsList) {
        this.gpsPointsList = gpsPointsList;
    }
    public Long getActivityId() {
        return activityId;
    }
    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }
}