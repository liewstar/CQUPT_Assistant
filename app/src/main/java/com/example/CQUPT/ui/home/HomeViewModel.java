package com.example.CQUPT.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.CQUPT.model.Course;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<List<Course>> mCourses;
    private final MutableLiveData<String> mCurrentDate;

    public HomeViewModel() {
        mCourses = new MutableLiveData<>();
        mCurrentDate = new MutableLiveData<>();
        loadCoursesForDate(new Date()); // 加载当天课程
        // 设置当前日期
        mCurrentDate.setValue("2024年1月1日 星期一");
    }

    public LiveData<List<Course>> getCourses() {
        return mCourses;
    }

    public LiveData<String> getCurrentDate() {
        return mCurrentDate;
    }

    public void setCurrentDate(String date) {
        mCurrentDate.setValue(date);
    }

    public void loadCoursesForDate(Date date) {
        // TODO: 在实际应用中，这里应该从数据库或网络加载指定日期的课程数据
        // 现在使用模拟数据进行演示
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        
        List<Course> courses = new ArrayList<>();
        
        // 根据星期几返回不同的课程安排
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                courses.add(new Course("高等数学", "08:00", "09:40", "教学楼2-301", "张三", 1, 16));
                courses.add(new Course("大学英语", "10:00", "11:40", "教学楼3-201", "李四", 1, 12));
                break;
            case Calendar.TUESDAY:
                courses.add(new Course("Java程序设计", "14:00", "15:40", "实验楼1-501", "王五", 2, 17));
                courses.add(new Course("数据结构", "16:00", "17:40", "教学楼4-401", "赵六", 3, 15));
                break;
            case Calendar.WEDNESDAY:
                courses.add(new Course("计算机网络", "08:00", "09:40", "教学楼1-301", "孙七", 1, 14));
                courses.add(new Course("操作系统", "10:00", "11:40", "实验楼2-401", "周八", 4, 16));
                break;
            case Calendar.THURSDAY:
                courses.add(new Course("软件工程", "14:00", "15:40", "教学楼5-201", "吴九", 2, 15));
                courses.add(new Course("数据库系统", "16:00", "17:40", "实验楼3-301", "郑十", 1, 13));
                break;
            case Calendar.FRIDAY:
                courses.add(new Course("计算机组成原理", "08:00", "09:40", "教学楼6-401", "刘一", 3, 18));
                courses.add(new Course("编译原理", "10:00", "11:40", "教学楼2-501", "陈二", 5, 16));
                break;
            // 周末没有课程
            case Calendar.SATURDAY:
            case Calendar.SUNDAY:
                break;
        }
        
        mCourses.setValue(courses);
    }
}