package com.example.CQUPT.utils;

import android.app.AppOpsManager;
import android.content.Context;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.app.Activity;
import android.net.Uri;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AppUsageManager {
    private static final String TAG = "AppUsageManager";
    private final Context context;
    private final UsageStatsManager usageStatsManager;
    private AlertDialog currentDialog;

    public AppUsageManager(Context context) {
        this.context = context;
        this.usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
    }

    public boolean hasUsagePermission() {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    public void requestUsagePermission() {
        // 如果已经有对话框在显示，就不要再显示新的
        if (currentDialog != null && currentDialog.isShowing()) {
            return;
        }

        currentDialog = new AlertDialog.Builder(context)
            .setTitle("需要权限")
            .setMessage("为了统计应用使用时间，需要授予“使用情况访问权限”。\n\n请在接下来的设置页面中找到“邮兵工具箱”，并开启其使用情况访问权限。")
            .setPositiveButton("去设置", (dialog, which) -> {
                try {
                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                    intent.setData(uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Exception e) {
                    // 如果无法直接跳转到应用的权限设置页面，就跳转到通用的使用情况访问页面
                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            })
            .setNegativeButton("取消", null)
            .setOnDismissListener(dialog -> {
                if (currentDialog == dialog) {
                    currentDialog = null;
                }
            })
            .create();

        currentDialog.show();
    }

    public long getAppUsageTime(String packageName, long startTime, long endTime) {
        try {
            List<UsageStats> stats = usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY,
                    startTime,
                    endTime
            );

            if (stats != null) {
                for (UsageStats usageStats : stats) {
                    if (usageStats.getPackageName().equals(packageName)) {
                        return usageStats.getTotalTimeInForeground();
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting app usage time", e);
        }
        return 0;
    }

    public long getTodayAppUsageTime(String packageName) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startTime = calendar.getTimeInMillis();
        long endTime = System.currentTimeMillis();

        return getAppUsageTime(packageName, startTime, endTime);
    }

    public String getFormattedUsageTime(long timeInMillis) {
        long hours = TimeUnit.MILLISECONDS.toHours(timeInMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis) % 60;

        if (hours > 0) {
            return String.format("%d小时%d分钟", hours, minutes);
        } else {
            return String.format("%d分钟", minutes);
        }
    }

    public boolean isExcessiveUsage(String packageName, long thresholdMinutes) {
        long usageTime = getTodayAppUsageTime(packageName);
        return TimeUnit.MILLISECONDS.toMinutes(usageTime) >= thresholdMinutes;
    }
}
