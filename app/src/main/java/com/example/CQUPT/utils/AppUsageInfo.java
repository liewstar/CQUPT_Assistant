package com.example.CQUPT.utils;

public class AppUsageInfo {
    private final String appName;
    private final String usageTime;

    public AppUsageInfo(String appName, String usageTime) {
        this.appName = appName;
        this.usageTime = usageTime;
    }

    public String getAppName() {
        return appName;
    }

    public String getUsageTime() {
        return usageTime;
    }
}
