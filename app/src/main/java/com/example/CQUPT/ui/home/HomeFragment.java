package com.example.CQUPT.ui.home;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import com.example.CQUPT.ui.settings.SettingsFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

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
    private Calendar semesterStartDate;

    public View onCreateView(@NonNull LayoutInflater inflater,
                           ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentClassesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initializeFormats();
        setupRecyclerView();
        setupDateNavigation();
        setupRetryButton();
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

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.recyclerCourses;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        courseAdapter = new CourseAdapter(new ArrayList<>());
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

    private void setupRetryButton() {
        binding.retryButton.setOnClickListener(v -> {
            loadCoursesForDate(currentDate.getTime());
        });
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
        binding.buttonDate.setText(String.format("%s %s", dateStr, weekDayStr));
    }

    private void loadCoursesForDate(Date date) {
        homeViewModel.loadCoursesForDate(date);
    }

    private void observeViewModel() {
        // 观察课程数据
        homeViewModel.getCourses().observe(getViewLifecycleOwner(), courses -> {
            courseAdapter.setCourses(courses);
            updateViewVisibility(courses.isEmpty());
        });

        // 观察加载状态
        homeViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.loadingProgress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.recyclerCourses.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            binding.errorCard.setVisibility(View.GONE);
        });

        // 观察错误信息
        homeViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                if (errorMessage.equals("请先在设置中配置学号")) {
                    showStudentIdError();
                } else {
                    showError(errorMessage);
                }
            } else {
                binding.errorCard.setVisibility(View.GONE);
            }
        });
    }

    private void updateViewVisibility(boolean isEmpty) {
        binding.emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        binding.recyclerCourses.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void showError(String message) {
        binding.errorCard.setVisibility(View.VISIBLE);
        binding.errorText.setText(message);
        binding.recyclerCourses.setVisibility(View.GONE);
        binding.emptyView.setVisibility(View.GONE);
    }

    private void showStudentIdError() {
        binding.errorCard.setVisibility(View.VISIBLE);
        binding.errorText.setText("请先设置学号才能查看课表");
        binding.retryButton.setText("去设置");
        binding.retryButton.setOnClickListener(v -> {
            // 跳转到设置页面
            startActivity(new Intent(requireContext(), SettingsFragment.class));
        });
        binding.recyclerCourses.setVisibility(View.GONE);
        binding.emptyView.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}