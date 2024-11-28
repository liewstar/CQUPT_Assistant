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
    private UsageStatsManager usageStatsManager;
    private AlertDialog currentDialog;

    public AppUsageManager(Context context) {
        this.context = context;
        try {
            this.usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing UsageStatsManager", e);
        }
    }

    public boolean hasUsagePermission() {
        try {
            AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            if (appOps != null) {
                int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, 
                    android.os.Process.myUid(), context.getPackageName());
                return mode == AppOpsManager.MODE_ALLOWED;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking usage permission", e);
        }
        return false;
    }

    private boolean isMIUI() {
        String manufacturer = android.os.Build.MANUFACTURER;
        String brand = android.os.Build.BRAND;
        return "xiaomi".equalsIgnoreCase(manufacturer) || "xiaomi".equalsIgnoreCase(brand) || 
               "redmi".equalsIgnoreCase(manufacturer) || "redmi".equalsIgnoreCase(brand);
    }

    private void requestMIUIPermission() {
        try {
            // MIUI 特定的权限设置页面
            Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.putExtra("extra_pkgname", context.getPackageName());
            intent.setClassName("com.miui.securitycenter",
                    "com.miui.permcenter.permissions.PermissionsEditorActivity");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error launching MIUI permission settings", e);
            // 如果MIUI特定页面失败，回退到标准权限页面
            openStandardPermissionSettings();
        }
    }

    private void openStandardPermissionSettings() {
        try {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            intent.setData(uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error launching standard permission settings", e);
            try {
                // 如果无法直接跳转到应用的权限设置页面，就跳转到通用的使用情况访问页面
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception e2) {
                Log.e(TAG, "Error launching general settings", e2);
            }
        }
    }

    public void requestUsagePermission() {
        try {
            // 如果已经有对话框在显示，就不要再显示新的
            if (currentDialog != null && currentDialog.isShowing()) {
                return;
            }

            if (!(context instanceof Activity)) {
                Log.e(TAG, "Context is not an Activity");
                return;
            }

            Activity activity = (Activity) context;
            if (activity.isFinishing() || activity.isDestroyed()) {
                Log.e(TAG, "Activity is finishing or destroyed");
                return;
            }

            currentDialog = new AlertDialog.Builder(context)
                .setTitle("需要权限")
                .setMessage(isMIUI() ? 
                    "为了统计应用使用时间，需要授予“使用情况访问权限”。\n\n请在接下来的MIUI权限设置页面中找到“使用情况访问权限”并开启。" :
                    "为了统计应用使用时间，需要授予“使用情况访问权限”。\n\n请在接下来的设置页面中找到本应用，并开启其使用情况访问权限。")
                .setPositiveButton("去设置", (dialog, which) -> {
                    if (isMIUI()) {
                        requestMIUIPermission();
                    } else {
                        openStandardPermissionSettings();
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
        } catch (Exception e) {
            Log.e(TAG, "Error showing permission dialog", e);
        }
    }

    public long getAppUsageTime(String packageName, long startTime, long endTime) {
        if (usageStatsManager == null || !hasUsagePermission()) {
            return 0;
        }

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
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long startTime = calendar.getTimeInMillis();
            long endTime = System.currentTimeMillis();

            return getAppUsageTime(packageName, startTime, endTime);
        } catch (Exception e) {
            Log.e(TAG, "Error getting today's app usage time", e);
            return 0;
        }
    }

    public String getFormattedUsageTime(long timeInMillis) {
        try {
            long hours = TimeUnit.MILLISECONDS.toHours(timeInMillis);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis) % 60;

            if (hours > 0) {
                return String.format("%d小时%d分钟", hours, minutes);
            } else {
                return String.format("%d分钟", minutes);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error formatting usage time", e);
            return "0分钟";
        }
    }

    public boolean isExcessiveUsage(String packageName, long thresholdMinutes) {
        try {
            if (usageStatsManager == null || !hasUsagePermission()) {
                return false;
            }
            long usageTime = getTodayAppUsageTime(packageName);
            return TimeUnit.MILLISECONDS.toMinutes(usageTime) >= thresholdMinutes;
        } catch (Exception e) {
            Log.e(TAG, "Error checking excessive usage", e);
            return false;
        }
    }
}
