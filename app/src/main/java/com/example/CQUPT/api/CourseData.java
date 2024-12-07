package com.example.CQUPT.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CourseData {
    @SerializedName("course_id")
    private String courseId;

    @SerializedName("course_name")
    private String courseName;

    @SerializedName("class_id")
    private String classId;

    @SerializedName("class_name")
    private String className;

    @SerializedName("teacher_name")
    private String teacherName;

    @SerializedName("course_type")
    private String courseType;

    @SerializedName("exam_type")
    private String examType;

    private String seat;
    private String qualification;

    @SerializedName("schedule_id")
    private String scheduleId;

    private String lecturer;

    @SerializedName("chief_invigilator")
    private String chiefInvigilator;

    @SerializedName("deputy_invigilators")
    private List<String> deputyInvigilators;

    // Getters
    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getClassId() {
        return classId;
    }

    public String getClassName() {
        return className;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getCourseType() {
        return courseType;
    }

    public String getExamType() {
        return examType;
    }

    public String getSeat() {
        return seat;
    }

    public String getQualification() {
        return qualification;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public String getLecturer() {
        return lecturer;
    }

    public String getChiefInvigilator() {
        return chiefInvigilator;
    }

    public List<String> getDeputyInvigilators() {
        return deputyInvigilators;
    }
}
