package com.example.CQUPT.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.CQUPT.R;

import java.util.ArrayList;
import java.util.List;

public class CourseSuccessAdapter extends RecyclerView.Adapter<CourseSuccessAdapter.ViewHolder> {
    private List<String> successCourses = new ArrayList<>();

    public void addSuccessCourse(String courseName) {
        successCourses.add(courseName);
        notifyItemInserted(successCourses.size() - 1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_success_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.courseNameText.setText(successCourses.get(position));
    }

    @Override
    public int getItemCount() {
        return successCourses.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView courseNameText;

        ViewHolder(View view) {
            super(view);
            courseNameText = view.findViewById(R.id.course_name_text);
        }
    }
}
