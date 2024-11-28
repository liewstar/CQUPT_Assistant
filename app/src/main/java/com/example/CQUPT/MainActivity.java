package com.example.CQUPT;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.example.CQUPT.databinding.ActivityMainBinding;
import com.example.CQUPT.utils.AppUsageManager;
import com.example.CQUPT.utils.UsageWarningDialog;
import com.google.android.material.navigation.NavigationView;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private NavController navController;
    private AppUsageManager appUsageManager;
    private SharedPreferences sharedPreferences;

    private static final String PREF_APP_NOTIFICATION_ENABLED = "app_notification_enabled";
    private static final String PREF_SELECTED_APPS = "selected_apps";
    private static final String PREF_TIME_THRESHOLD = "time_threshold";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0);
        TextView everyDay = headerView.findViewById(R.id.header_everyday);
        everyDay.setText("这是每日一言");

        // 配置导航
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_network, R.id.nav_news, R.id.nav_course_selection, R.id.settingsFragment, R.id.aboutFragment)
                .setOpenableLayout(drawer)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        appUsageManager = new AppUsageManager(this);

        // 根据设置选择启动页面
        if (savedInstanceState == null) { // 只在首次创建时设置
            String startPage = sharedPreferences.getString("start_page", "课程表");

            int startDestination = R.id.nav_home; // 默认为课程表
            switch (startPage) {
                case "校园公告":
                    startDestination = R.id.nav_news;
                    break;
                case "校园网登录":
                    startDestination = R.id.nav_network;
                    break;
            }

            // 更新导航图的起始目的地
            NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.mobile_navigation);
            navGraph.setStartDestination(startDestination);
            navController.setGraph(navGraph);
        }
    }

    private void checkAppUsage() {
        boolean notificationEnabled = sharedPreferences.getBoolean(PREF_APP_NOTIFICATION_ENABLED, false);
        if (!notificationEnabled) {
            return;
        }

        // 如果功能已启用但没有权限，才请求权限
        if (!appUsageManager.hasUsagePermission()) {
            appUsageManager.requestUsagePermission();
            return;
        }

        // 已有权限，检查应用使用情况
        checkAppUsageWithPermission();
    }

    private void checkAppUsageWithPermission() {
        Set<String> selectedApps = sharedPreferences.getStringSet(PREF_SELECTED_APPS, new HashSet<>());
        int timeThreshold = sharedPreferences.getInt(PREF_TIME_THRESHOLD, 30); // 默认30分钟

        for (String packageName : selectedApps) {
            if (appUsageManager.isExcessiveUsage(packageName, timeThreshold)) {
                String appName = getAppName(packageName);
                String usageTime = appUsageManager.getFormattedUsageTime(
                        appUsageManager.getTodayAppUsageTime(packageName)
                );

                UsageWarningDialog dialog = new UsageWarningDialog(this, appName, usageTime);
                dialog.show();
                break; // 只显示一个提醒
            }
        }
    }

    private String getAppName(String packageName) {
        try {
            return getPackageManager().getApplicationLabel(
                    getPackageManager().getApplicationInfo(packageName, 0)
            ).toString();
        } catch (Exception e) {
            return packageName;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 只有当功能启用时才检查使用情况
        boolean notificationEnabled = sharedPreferences.getBoolean(PREF_APP_NOTIFICATION_ENABLED, false);
        if (notificationEnabled && appUsageManager.hasUsagePermission()) {
            // 如果已经有权限，直接检查使用情况
            checkAppUsageWithPermission();
        } else if (notificationEnabled) {
            checkAppUsage();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}