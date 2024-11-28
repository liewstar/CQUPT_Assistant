package com.example.CQUPT.ui.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.CQUPT.R;
import com.example.CQUPT.ui.login.LoginWebViewActivity;

public class SettingsFragment extends Fragment {

    private TextView startPageValue;
    private TextView sessionValue;
    private Switch appNotificationSwitch;
    private SharedPreferences sharedPreferences;
    private static final String PREF_START_PAGE = "start_page";
    private static final String PREF_SESSION_ID = "session_id";
    private static final String PREF_APP_NOTIFICATION_ENABLED = "app_notification_enabled";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        
        // 初始化视图
        startPageValue = view.findViewById(R.id.start_page_value);
        sessionValue = view.findViewById(R.id.session_value);
        appNotificationSwitch = view.findViewById(R.id.app_notification_switch);
        View startPageContainer = view.findViewById(R.id.start_page_container);
        View sessionContainer = view.findViewById(R.id.session_container);
        View appNotificationContainer = view.findViewById(R.id.app_notification_container);

        // 设置当前值
        updateStartPageValue();
        updateSessionValue();
        boolean notificationEnabled = sharedPreferences.getBoolean(PREF_APP_NOTIFICATION_ENABLED, false);
        appNotificationSwitch.setChecked(notificationEnabled);

        // 设置点击事件
        startPageContainer.setOnClickListener(v -> showStartPageDialog());
        sessionContainer.setOnClickListener(v -> startLoginWebView());
        appNotificationContainer.setOnClickListener(v -> startAppNotificationSettings());
        appNotificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean(PREF_APP_NOTIFICATION_ENABLED, isChecked).apply();
        });

        return view;
    }

    private void updateStartPageValue() {
        String currentStartPage = sharedPreferences.getString(PREF_START_PAGE, "课程表");
        startPageValue.setText(currentStartPage);
    }

    private void updateSessionValue() {
        String sessionId = sharedPreferences.getString(PREF_SESSION_ID, "未设置");
        // 只显示session的前6位，其余用***代替
        if (!"未设置".equals(sessionId) && sessionId.length() > 6) {
            sessionId = sessionId.substring(0, 6) + "***";
        }
        sessionValue.setText(sessionId);
    }

    private void startLoginWebView() {
        Intent intent = new Intent(requireContext(), LoginWebViewActivity.class);
        startActivity(intent);
    }

    private void startAppNotificationSettings() {
        Intent intent = new Intent(requireContext(), AppNotificationSettingsActivity.class);
        startActivity(intent);
    }

    private void showStartPageDialog() {
        String[] pages = {"课程表", "校园公告", "校园网登录"};
        String currentStartPage = sharedPreferences.getString(PREF_START_PAGE, "课程表");
        int currentSelection = 0;
        for (int i = 0; i < pages.length; i++) {
            if (pages[i].equals(currentStartPage)) {
                currentSelection = i;
                break;
            }
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("选择启动页面")
                .setSingleChoiceItems(pages, currentSelection, (dialog, which) -> {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(PREF_START_PAGE, pages[which]);
                    editor.apply();
                    updateStartPageValue();
                    dialog.dismiss();
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
