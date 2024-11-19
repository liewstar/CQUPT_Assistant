package com.example.CQUPT.ui.ai;

import android.os.Bundle;
import android.text.TextUtils;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;
import com.example.CQUPT.R;
import com.example.CQUPT.ai.AIManager;
import com.google.android.material.snackbar.Snackbar;

public class AISettingsFragment extends PreferenceFragmentCompat {

    private AIManager aiManager;
    private SwitchPreferenceCompat enableAI;
    private ListPreference aiProvider;
    private EditTextPreference apiKey;
    private SwitchPreferenceCompat enableOCR;
    private SwitchPreferenceCompat enableVoice;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.ai_preferences, rootKey);
        aiManager = AIManager.getInstance(requireContext());

        // 初始化偏好设置
        enableAI = findPreference("enable_ai");
        aiProvider = findPreference("ai_provider");
        apiKey = findPreference("api_key");
        enableOCR = findPreference("enable_ocr");
        enableVoice = findPreference("enable_voice");

        // 设置依赖关系
        aiProvider.setDependency("enable_ai");
        apiKey.setDependency("enable_ai");
        enableOCR.setDependency("enable_ai");
        enableVoice.setDependency("enable_ai");

        // 设置API密钥变更监听
        if (apiKey != null) {
            apiKey.setOnPreferenceChangeListener((preference, newValue) -> {
                String key = (String) newValue;
                if (TextUtils.isEmpty(key)) {
                    showMessage("API密钥不能为空");
                    return false;
                }
                aiManager.saveApiKey(key);
                showMessage("API密钥已保存");
                return true;
            });
        }

        // 设置AI提供商变更监听
        if (aiProvider != null) {
            aiProvider.setOnPreferenceChangeListener((preference, newValue) -> {
                String provider = (String) newValue;
                // 清除旧的API密钥
                aiManager.saveApiKey("");
                apiKey.setText("");
                showMessage("已切换至" + aiProvider.getEntries()[aiProvider.findIndexOfValue(provider)]);
                return true;
            });
        }

        // 设置功能开关监听
        if (enableAI != null) {
            enableAI.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean enabled = (boolean) newValue;
                if (enabled && TextUtils.isEmpty(aiManager.getApiKey())) {
                    showMessage("请先设置API密钥");
                    return false;
                }
                return true;
            });
        }
    }

    private void showMessage(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
        }
    }
}
