package com.example.CQUPT.ui.home;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.CQUPT.adapter.CourseAdapter;
import com.example.CQUPT.databinding.FragmentClassesBinding;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentClassesBinding binding;
    private HomeViewModel homeViewModel;
    private CourseAdapter courseAdapter;
    private Calendar currentDate;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat weekDayFormat;
    private Calendar semesterStartDate; // 添加学期开始日期

    public View onCreateView(@NonNull LayoutInflater inflater,
                           ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentClassesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initializeFormats();
        setupRecyclerView();
        setupDateNavigation();
        observeViewModel();
        updateDateDisplay();

        return root;
    }

    private void initializeFormats() {
        currentDate = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy年M月d日", Locale.CHINESE);
        weekDayFormat = new SimpleDateFormat("EEEE", Locale.CHINESE);
        
        // 设置学期开始日期（这里假设是2024年2月26日，你需要根据实际情况修改）
        semesterStartDate = Calendar.getInstance();
        semesterStartDate.set(2024, Calendar.FEBRUARY, 26);
    }

    private int getCurrentWeek() {
        long diff = currentDate.getTimeInMillis() - semesterStartDate.getTimeInMillis();
        int daysDiff = (int) (diff / (24 * 60 * 60 * 1000));
        return (daysDiff / 7) + 1;
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.recyclerCourses;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        courseAdapter = new CourseAdapter(new ArrayList<>(), getCurrentWeek());
        recyclerView.setAdapter(courseAdapter);
    }

    private void setupDateNavigation() {
        ImageButton prevButton = binding.buttonPrevDay;
        ImageButton nextButton = binding.buttonNextDay;
        MaterialButton dateButton = binding.buttonDate;

        prevButton.setOnClickListener(v -> navigateDay(-1));
        nextButton.setOnClickListener(v -> navigateDay(1));
        dateButton.setOnClickListener(v -> showDatePicker());
    }

    private void navigateDay(int offset) {
        currentDate.add(Calendar.DAY_OF_MONTH, offset);
        updateDateDisplay();
        loadCoursesForDate(currentDate.getTime());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            requireContext(),
            (view, year, month, dayOfMonth) -> {
                currentDate.set(year, month, dayOfMonth);
                updateDateDisplay();
                loadCoursesForDate(currentDate.getTime());
            },
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        String dateStr = dateFormat.format(currentDate.getTime());
        String weekDayStr = weekDayFormat.format(currentDate.getTime());
        int currentWeek = getCurrentWeek();
        binding.buttonDate.setText(String.format("%s %s (第%d周)", dateStr, weekDayStr, currentWeek));
        
        // 更新适配器中的当前周
        if (courseAdapter != null) {
            courseAdapter.setCurrentWeek(currentWeek);
        }
    }

    private void loadCoursesForDate(Date date) {
        homeViewModel.loadCoursesForDate(date);
    }

    private void observeViewModel() {
        homeViewModel.getCourses().observe(getViewLifecycleOwner(), courses -> {
            courseAdapter.setCourses(courses);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}