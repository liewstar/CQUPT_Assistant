package com.example.CQUPT.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.CQUPT.R;
import com.example.CQUPT.model.Course;

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
        private final TextView courseTimeText;
        private final TextView courseNameText;
        private final TextView locationText;
        private final TextView teacherText;
        private final TextView weekRangeText;
        private final TextView currentWeekText;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseTimeText = itemView.findViewById(R.id.courseTimeText);
            courseNameText = itemView.findViewById(R.id.courseNameText);
            locationText = itemView.findViewById(R.id.locationText);
            teacherText = itemView.findViewById(R.id.teacherText);
            weekRangeText = itemView.findViewById(R.id.weekRangeText);
            currentWeekText = itemView.findViewById(R.id.currentWeekText);
        }

        public void bind(Course course, int currentWeek) {
            courseTimeText.setText(course.getTimeRange());
            courseNameText.setText(course.getName());
            locationText.setText(course.getLocation());
            teacherText.setText(course.getTeacher());
            weekRangeText.setText(course.getWeekRange());
            currentWeekText.setText(String.format("第%d周", currentWeek));
        }
    }
}
