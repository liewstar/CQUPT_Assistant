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
    private int currentWeek;

    public CourseAdapter(List<Course> courses, int currentWeek) {
        this.courses = courses;
        this.currentWeek = currentWeek;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
        notifyDataSetChanged();
    }

    public void setCurrentWeek(int currentWeek) {
        this.currentWeek = currentWeek;
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
        holder.bind(course, currentWeek);
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

        public void bind(Course course, int currentWeek) {
            Context context = itemView.getContext();
            
            // 设置基本信息
            courseTimeText.setText(course.getTimeRange());
            courseNameText.setText(course.getName());
            locationText.setText(course.getLocation());
            teacherText.setText(course.getTeacher());
            weekRangeText.setText(course.getWeekRange());
            currentWeekText.setText(String.format("第%d周", currentWeek));

            // 判断课程是否在当前周
            boolean isInCurrentWeek = currentWeek >= course.getStartWeek() && currentWeek <= course.getEndWeek();
            
            // 根据是否在当前周设置卡片样式
            if (isInCurrentWeek) {
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.purple_50));
                cardView.setStrokeColor(ContextCompat.getColor(context, R.color.purple_200));
                cardView.setStrokeWidth(2);
                courseNameText.setTextColor(ContextCompat.getColor(context, R.color.purple_700));
            } else {
                cardView.setCardBackgroundColor(Color.WHITE);
                cardView.setStrokeColor(Color.TRANSPARENT);
                cardView.setStrokeWidth(0);
                courseNameText.setTextColor(ContextCompat.getColor(context, android.R.color.black));
            }

            // 设置点击事件显示详情
            cardView.setOnClickListener(v -> showCourseDetail(context, course, currentWeek));
        }

        private void showCourseDetail(Context context, Course course, int currentWeek) {
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_course_detail, null);

            // 设置详情对话框的内容
            TextView courseNameTitle = dialogView.findViewById(R.id.courseNameTitle);
            TextView timeText = dialogView.findViewById(R.id.timeText);
            TextView weekRangeDetailText = dialogView.findViewById(R.id.weekRangeDetailText);
            TextView locationDetailText = dialogView.findViewById(R.id.locationDetailText);
            TextView teacherDetailText = dialogView.findViewById(R.id.teacherDetailText);

            courseNameTitle.setText(course.getName());
            timeText.setText(course.getTimeRange());
            weekRangeDetailText.setText(String.format("%s（当前第%d周）", course.getWeekRange(), currentWeek));
            locationDetailText.setText(course.getLocation());
            teacherDetailText.setText(course.getTeacher());

            // 创建并显示对话框
            new MaterialAlertDialogBuilder(context)
                    .setView(dialogView)
                    .setPositiveButton("关闭", null)
                    .show();
        }
    }
}
