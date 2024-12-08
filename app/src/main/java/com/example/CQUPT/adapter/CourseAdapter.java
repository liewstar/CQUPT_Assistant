package com.example.CQUPT.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.CQUPT.R;
import com.example.CQUPT.model.Course;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private List<Course> courses;

    public CourseAdapter(List<Course> courses) {
        this.courses = courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.bind(course);
    }

    @Override
    public int getItemCount() {
        return courses != null ? courses.size() : 0;
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final TextView courseTimeText;
        private final TextView courseNameText;
        private final TextView locationText;
        private final TextView teacherText;
        private final TextView weekRangeText;
        private final TextView currentWeekText;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            courseTimeText = itemView.findViewById(R.id.courseTimeText);
            courseNameText = itemView.findViewById(R.id.courseNameText);
            locationText = itemView.findViewById(R.id.locationText);
            teacherText = itemView.findViewById(R.id.teacherText);
            weekRangeText = itemView.findViewById(R.id.weekRangeText);
            currentWeekText = itemView.findViewById(R.id.currentWeekText);
        }

        public void bind(Course course) {
            Context context = itemView.getContext();

            // 设置基本信息
            courseTimeText.setText(course.getTimeRange());
            courseNameText.setText(course.getName());
            locationText.setText(course.getLocation());
            teacherText.setText(course.getTeacher());
            weekRangeText.setText(course.getWeekRange());
            currentWeekText.setText(String.format("第%d周", course.getCurrentWeek()));

            // 判断课程是否在当前周
            boolean isInCurrentWeek = course.getCurrentWeek() >= course.getStartWeek() 
                && course.getCurrentWeek() <= course.getEndWeek();

            // 根据是否在当前周设置卡片样式
//            if (isInCurrentWeek) {
//                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.purple_50));
//                cardView.setStrokeColor(ContextCompat.getColor(context, R.color.purple_200));
//                cardView.setStrokeWidth(2);
//                courseNameText.setTextColor(ContextCompat.getColor(context, R.color.purple_700));
//            } else {
//                cardView.setCardBackgroundColor(Color.WHITE);
//                cardView.setStrokeColor(Color.TRANSPARENT);
//                cardView.setStrokeWidth(0);
//                courseNameText.setTextColor(Color.BLACK);
//            }

            cardView.setCardBackgroundColor(Color.WHITE);
            cardView.setStrokeColor(Color.TRANSPARENT);
            cardView.setStrokeWidth(0);
            courseNameText.setTextColor(Color.BLACK);

            // 设置点击事件显示课程详情
            cardView.setOnClickListener(v -> showCourseDetailDialog(context, course));
        }

        private void showCourseDetailDialog(Context context, Course course) {
            new MaterialAlertDialogBuilder(context)
                    .setTitle(course.getName())
                    .setMessage(String.format(
                            "时间: %s\n位置: %s\n教师: %s\n周数: %s\n当前周: 第%d周",
                            course.getTimeRange(),
                            course.getLocation(),
                            course.getTeacher(),
                            course.getWeekRange(),
                            course.getCurrentWeek()
                    ))
                    .setPositiveButton("确定", null)
                    .show();
        }
    }
}
