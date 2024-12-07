package com.example.CQUPT.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TimetableResponse {
    private int status;
    private String message;
    private DataWrapper data;

    public static class DataWrapper {
        private int code;
        private String msg;
        private CourseData data;

        public static class CourseData {
            @SerializedName("course_schedules")
            private List<CourseSchedule> courseSchedules;

            public List<CourseSchedule> getCourseSchedules() {
                return courseSchedules;
            }
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        public CourseData getData() {
            return data;
        }
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public DataWrapper getData() {
        return data;
    }

    // 便捷方法，直接获取课程列表
    public List<CourseSchedule> getCourseSchedules() {
        if (data != null && data.getData() != null) {
            return data.getData().getCourseSchedules();
        }
        return null;
    }

    // 检查响应是否成功
    public boolean isSuccessful() {
        return status == 200 && data != null && data.getCode() == 0;
    }
}
