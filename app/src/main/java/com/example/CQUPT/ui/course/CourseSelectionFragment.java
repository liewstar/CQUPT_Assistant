package com.example.CQUPT.ui.course;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.CQUPT.adapter.CourseSuccessAdapter;
import com.example.CQUPT.databinding.FragmentCourseSelectionBinding;
import com.google.android.material.button.MaterialButton;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CourseSelectionFragment extends Fragment {
    private FragmentCourseSelectionBinding binding;
    private CourseSuccessAdapter adapter;
    private ScheduledExecutorService executor;
    private Handler mainHandler;
    private boolean isRunning = false;
    private static final String PREF_SESSION_ID = "session_id";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCourseSelectionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupViews();
        return root;
    }

    private void setupViews() {
        mainHandler = new Handler(Looper.getMainLooper());
        adapter = new CourseSuccessAdapter();
        binding.successList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.successList.setAdapter(adapter);

        // 从SharedPreferences获取Session并设置
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String savedSession = prefs.getString(PREF_SESSION_ID, "");
        binding.sessionInput.setText(savedSession);

        binding.startButton.setOnClickListener(v -> {
            if (!isRunning) {
                startCourseSelection();
            } else {
                stopCourseSelection();
            }
        });

        binding.speedSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Speed changes will be applied on next start
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void startCourseSelection() {
        // 从SharedPreferences获取最新的Session
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String session = prefs.getString(PREF_SESSION_ID, "");
        String courseNames = binding.courseNamesInput.getText().toString();

        if (session.isEmpty()) {
            Toast.makeText(requireContext(), "请先在设置页面配置Session", Toast.LENGTH_SHORT).show();
            return;
        }

        if (courseNames.isEmpty()) {
            Toast.makeText(requireContext(), "请填写课程名称", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> courses = Arrays.asList(courseNames.split(","));
        int speed = binding.speedSeekbar.getProgress();
        int delay = 100 - speed; // Convert progress to delay (0-100 to 100-0ms)

        isRunning = true;
        binding.startButton.setText("停止抢课");
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.statusText.setText("正在抢课中...");

        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(() -> {
            // Simulate course selection attempt
            for (String course : courses) {
                // Random success simulation (20% chance)
                if (new Random().nextInt(100) < 20) {
                    mainHandler.post(() -> {
                        adapter.addSuccessCourse(course.trim());
                        binding.statusText.setText("成功抢到: " + course.trim());
                    });
                }
            }
        }, 0, delay * 10L, TimeUnit.MILLISECONDS);
    }

    private void stopCourseSelection() {
        if (executor != null) {
            executor.shutdown();
        }
        isRunning = false;
        binding.startButton.setText("开始抢课");
        binding.progressBar.setVisibility(View.GONE);
        binding.statusText.setText("抢课已停止");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (executor != null) {
            executor.shutdown();
        }
        binding = null;
    }
}
