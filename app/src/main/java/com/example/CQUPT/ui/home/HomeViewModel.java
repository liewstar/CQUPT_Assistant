package com.example.CQUPT.ui.home;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import com.example.CQUPT.api.RetrofitClient;
import com.example.CQUPT.api.TimetableResponse;
import com.example.CQUPT.api.CourseSchedule;
import com.example.CQUPT.model.Course;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends AndroidViewModel {
    private static final String PREF_STUDENT_ID = "student_id";
    private final MutableLiveData<List<Course>> mCourses;
    private final MutableLiveData<String> mCurrentDate;
    private final MutableLiveData<Boolean> isLoading;
    private final MutableLiveData<String> errorMessage;
    private final SimpleDateFormat apiDateFormat;
    private final SimpleDateFormat apiTimeFormat;
    private final SharedPreferences sharedPreferences;

    public HomeViewModel(Application application) {
        super(application);
        mCourses = new MutableLiveData<>();
        mCurrentDate = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(false);
        errorMessage = new MutableLiveData<>();
        apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        apiTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);
        
        loadCoursesForDate(new Date());
    }

    public LiveData<List<Course>> getCourses() {
        return mCourses;
    }

    public LiveData<String> getCurrentDate() {
        return mCurrentDate;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void setCurrentDate(String date) {
        mCurrentDate.setValue(date);
    }

    public void loadCoursesForDate(Date date) {
        String studentId = sharedPreferences.getString(PREF_STUDENT_ID, null);
        if (studentId == null || studentId.equals("未设置")) {
            errorMessage.setValue("请先在设置中配置学号");
            return;
        }

        isLoading.setValue(true);
        RetrofitClient.getInstance()
                .getTimetableService()
                .getTimetable(studentId)
                .enqueue(new Callback<TimetableResponse>() {
                    @Override
                    public void onResponse(Call<TimetableResponse> call, Response<TimetableResponse> response) {
                        isLoading.setValue(false);
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccessful()) {
                            List<CourseSchedule> schedules = response.body().getCourseSchedules();
                            if (schedules != null) {
                                List<Course> coursesForDate = filterCoursesForDate(schedules, date);
                                mCourses.setValue(coursesForDate);
                                if (coursesForDate.isEmpty()) {
                                    errorMessage.setValue("今天没有课程");
                                } else {
                                    errorMessage.setValue(null);
                                }
                            }
                        } else {
                            String error = response.body() != null ? response.body().getMessage() : "获取课程数据失败";
                            errorMessage.setValue(error);
                        }
                    }

                    @Override
                    public void onFailure(Call<TimetableResponse> call, Throwable t) {
                        isLoading.setValue(false);
                        errorMessage.setValue("网络请求失败: " + t.getMessage());
                    }
                });
    }

    private List<Course> filterCoursesForDate(List<CourseSchedule> schedules, Date targetDate) {
        List<Course> courses = new ArrayList<>();
        String targetDateStr = apiDateFormat.format(targetDate);

        for (CourseSchedule schedule : schedules) {
            try {
                if (schedule.getDate().equals(targetDateStr)) {
                    // 转换时间格式
                    String startTime = schedule.getStartTime().substring(0, 5); // 取"HH:mm"部分
                    String endTime = schedule.getEndTime().substring(0, 5);     // 取"HH:mm"部分

                    // 获取周数范围
                    List<Integer> weekNums = schedule.getWeekNums();
                    int startWeek = weekNums.isEmpty() ? 1 : weekNums.get(0);
                    int endWeek = weekNums.isEmpty() ? 1 : weekNums.get(weekNums.size() - 1);

                    Course course = new Course(
                            schedule.getTitle(),
                            startTime,
                            endTime,
                            schedule.getLocation(),
                            schedule.getData().getTeacherName(),
                            startWeek,
                            endWeek
                    );
                    courses.add(course);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return courses;
    }
}