package com.example.CQUPT.ui.settings;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.CQUPT.R;
import com.google.android.material.textfield.TextInputEditText;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppNotificationSettingsActivity extends AppCompatActivity {
    private RecyclerView appList;
    private TextInputEditText timeThresholdInput;
    private TextInputEditText searchInput;
    private SharedPreferences sharedPreferences;
    private static final String PREF_SELECTED_APPS = "selected_apps";
    private static final String PREF_TIME_THRESHOLD = "time_threshold";
    private List<AppInfo> appInfoList;
    private List<AppInfo> filteredAppList;
    private AppListAdapter adapter;
    private LinearLayout loadingLayout;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_notification_settings);

        // 设置工具栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("高频应用设置");
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        
        // 初始化视图
        timeThresholdInput = findViewById(R.id.timeThresholdInput);
        searchInput = findViewById(R.id.searchInput);
        appList = findViewById(R.id.appList);
        loadingLayout = findViewById(R.id.loadingLayout);
        appList.setLayoutManager(new LinearLayoutManager(this));

        // 设置已保存的时间阈值
        int savedThreshold = sharedPreferences.getInt(PREF_TIME_THRESHOLD, 30);
        timeThresholdInput.setText(String.valueOf(savedThreshold));

        // 添加文本变化监听器，实时保存时间阈值
        timeThresholdInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                saveTimeThreshold();
            }
        });

        // 初始化应用列表
        appInfoList = new ArrayList<>();
        filteredAppList = new ArrayList<>();
        adapter = new AppListAdapter(filteredAppList, (packageName, isChecked) -> {
            Set<String> currentSelected = new HashSet<>(sharedPreferences.getStringSet(PREF_SELECTED_APPS, new HashSet<>()));
            if (isChecked) {
                currentSelected.add(packageName);
            } else {
                currentSelected.remove(packageName);
            }
            sharedPreferences.edit().putStringSet(PREF_SELECTED_APPS, currentSelected).apply();
        });
        appList.setAdapter(adapter);

        // 添加搜索监听器
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filterApps(s.toString());
            }
        });

        // 异步加载应用列表
        loadInstalledAppsAsync();
    }

    private void filterApps(String query) {
        if (query.isEmpty()) {
            filteredAppList.clear();
            filteredAppList.addAll(appInfoList);
        } else {
            filteredAppList.clear();
            String lowerQuery = query.toLowerCase();
            for (AppInfo app : appInfoList) {
                if (app.appName.toLowerCase().contains(lowerQuery) ||
                    app.packageName.toLowerCase().contains(lowerQuery)) {
                    filteredAppList.add(app);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void saveTimeThreshold() {
        try {
            String input = timeThresholdInput.getText().toString();
            if (!input.isEmpty()) {
                int threshold = Integer.parseInt(input);
                if (threshold > 0) {
                    sharedPreferences.edit().putInt(PREF_TIME_THRESHOLD, threshold).apply();
                }
            }
        } catch (NumberFormatException e) {
            // 保持原有值
        }
    }

    private void loadInstalledAppsAsync() {
        loadingLayout.setVisibility(View.VISIBLE);
        appList.setVisibility(View.GONE);

        executor.execute(() -> {
            // 在后台线程加载应用列表
            List<AppInfo> loadedApps = loadInstalledApps();
            
            // 在主线程更新UI
            runOnUiThread(() -> {
                appInfoList.clear();
                appInfoList.addAll(loadedApps);
                filteredAppList.clear();
                filteredAppList.addAll(loadedApps);
                adapter.notifyDataSetChanged();
                loadingLayout.setVisibility(View.GONE);
                appList.setVisibility(View.VISIBLE);
            });
        });
    }

    private List<AppInfo> loadInstalledApps() {
        List<AppInfo> loadedApps = new ArrayList<>();
        PackageManager pm = getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_META_DATA | PackageManager.GET_ACTIVITIES);
        Set<String> selectedApps = sharedPreferences.getStringSet(PREF_SELECTED_APPS, new HashSet<>());

        for (PackageInfo packageInfo : packages) {
            try {
                // 获取应用信息
                ApplicationInfo appInfo = pm.getApplicationInfo(packageInfo.packageName, 0);
                
                // 检查是否是系统应用
                boolean isSystemApp = (appInfo.flags & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0;
                
                // 检查是否有启动器活动
                Intent launchIntent = pm.getLaunchIntentForPackage(packageInfo.packageName);
                
                // 如果不是系统应用或者是有启动器的系统应用，就添加到列表
                if (!isSystemApp || launchIntent != null) {
                    AppInfo app = new AppInfo();
                    app.packageName = packageInfo.packageName;
                    app.appName = pm.getApplicationLabel(appInfo).toString();
                    app.isSelected = selectedApps.contains(packageInfo.packageName);
                    loadedApps.add(app);
                }
            } catch (PackageManager.NameNotFoundException e) {
                // 忽略无法获取信息的应用
            }
        }

        // 按应用名称排序
        Collections.sort(loadedApps, (a, b) -> a.appName.compareToIgnoreCase(b.appName));
        
        return loadedApps;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveTimeThreshold();
    }

    static class AppInfo {
        String packageName;
        String appName;
        boolean isSelected;
    }
}
