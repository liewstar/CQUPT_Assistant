package com.example.CQUPT.model;

public class Course {
    private String name;
    private String startTime;
    private String endTime;
    private String location;
    private String teacher;
    private int startWeek;
    private int endWeek;
    private int currentWeek;

    public Course(String name, String startTime, String endTime, String location, String teacher, int startWeek, int endWeek, int currentWeek) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.teacher = teacher;
        this.startWeek = startWeek;
        this.endWeek = endWeek;
        this.currentWeek = currentWeek;
    }

    public String getName() {
        return name;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getTimeRange() {
        return startTime + " - " + endTime;
    }

    public String getLocation() {
        return location;
    }

    public String getTeacher() {
        return teacher;
    }

    public int getStartWeek() {
        return startWeek;
    }

    public int getEndWeek() {
        return endWeek;
    }

    public int getCurrentWeek() { return currentWeek; }

    public String getWeekRange() {
        return String.format("第%d-%d周", startWeek, endWeek);
    }
}
