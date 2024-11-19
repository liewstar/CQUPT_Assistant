package com.example.CQUPT.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.CQUPT.R;

public class SettingsFragment extends Fragment {

    private TextView startPageValue;
    private SharedPreferences sharedPreferences;
    private static final String PREF_START_PAGE = "start_page";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        
        // 初始化视图
        startPageValue = view.findViewById(R.id.start_page_value);
        View startPageContainer = view.findViewById(R.id.start_page_container);

        // 设置当前值
        updateStartPageValue();

        // 设置点击事件
        startPageContainer.setOnClickListener(v -> showStartPageDialog());

        return view;
    }

    private void updateStartPageValue() {
        String currentStartPage = sharedPreferences.getString(PREF_START_PAGE, "课程表");
        startPageValue.setText(currentStartPage);
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
