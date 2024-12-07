package com.example.CQUPT.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TimetableApiService {
    /**
     * 获取课程表数据
     * @param studentId 学生学号
     * @return 课程表数据响应
     */
    @GET("api/timetable/{studentId}")
    Call<TimetableResponse> getTimetable(@Path("studentId") String studentId);
}
