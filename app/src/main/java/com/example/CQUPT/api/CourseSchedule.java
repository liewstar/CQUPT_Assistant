package com.example.CQUPT.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CourseSchedule {
    private String id;
    private int type;

    @SerializedName("type_id")
    private String typeId;

    private String date;

    @SerializedName("week_nums")
    private List<Integer> weekNums;

    @SerializedName("week_num")
    private int weekNum;

    @SerializedName("start_time")
    private String startTime;

    @SerializedName("end_time")
    private String endTime;

    @SerializedName("time_slots")
    private List<Integer> timeSlots;

    private String title;
    private String location;
    private String description;
    private CourseData data;

    // Getters
    public String getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public String getTypeId() {
        return typeId;
    }

    public String getDate() {
        return date;
    }

    public List<Integer> getWeekNums() {
        return weekNums;
    }

    public int getWeekNum() {
        return weekNum;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public List<Integer> getTimeSlots() {
        return timeSlots;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public CourseData getData() {
        return data;
    }
}
