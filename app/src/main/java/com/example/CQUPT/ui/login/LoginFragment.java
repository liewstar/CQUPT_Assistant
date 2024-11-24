package com.example.CQUPT.ui.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.CQUPT.R;
import com.example.CQUPT.databinding.FragmentLoginBinding;
import com.example.CQUPT.ui.HttpUtil;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);

        // 加载保存的数据
        loadSavedData();

        binding.loginButton.setOnClickListener(v -> handleLogin());

        return root;
    }

    private void loadSavedData() {
        binding.usernameInput.setText(sharedPreferences.getString("username", ""));
        binding.passwordInput.setText(sharedPreferences.getString("password", ""));

        // 加载运营商选择
        int savedOperatorId = sharedPreferences.getInt("operator", R.id.operator_telecom);
        binding.operatorGroup.check(savedOperatorId);

        // 加载客户端选择
        boolean isComputerClient = sharedPreferences.getBoolean("is_computer_client", false);
        binding.clientGroup.check(isComputerClient ? R.id.client_computer : R.id.client_mobile);
    }

    private void handleLogin() {
        String username = binding.usernameInput.getText().toString();
        String password = binding.passwordInput.getText().toString();

        // 验证输入
        if (username.isEmpty() || password.isEmpty()) {
            showError("用户名和密码不能为空");
            return;
        }

        // 获取选中的运营商
        Chip selectedOperatorChip = binding.operatorGroup.findViewById(binding.operatorGroup.getCheckedChipId());
        String operator;
        if (selectedOperatorChip != null) {
            if (selectedOperatorChip.getId() == R.id.operator_mobile) {
                operator = "cmcc";
            } else if (selectedOperatorChip.getId() == R.id.operator_telecom) {
                operator = "telecom";
            } else {
                operator = "unicom";
            }
        } else {
            showError("请选择运营商");
            return;
        }

        // 获取选中的客户端类型
        Chip selectedClientChip = binding.clientGroup.findViewById(binding.clientGroup.getCheckedChipId());
        if (selectedClientChip == null) {
            showError("请选择客户端类型");
            return;
        }
        boolean isComputerClient = selectedClientChip.getId() == R.id.client_computer;
        String computer = isComputerClient ? "0" : "1";

        // 保存数据
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putInt("operator", binding.operatorGroup.getCheckedChipId());
        editor.putBoolean("is_computer_client", isComputerClient);
        editor.apply();

        // 显示加载状态
        binding.loginButton.setEnabled(false);
        binding.loginButton.setText("登录中...");

        // 获取IP地址
        String localIPAddress = NetworkUtils.getLocalIPAddress();
        if (localIPAddress.isEmpty()) {
            showError("无法获取IP地址，请检查网络连接");
            resetLoginButton();
            return;
        }

        // 构建登录URL
        String url = "http://192.168.200.2:801/eportal/?c=Portal&a=login&callback=dr1003&login_method="
                + computer
                + "&user_account=%2C1%2C"
                + username
                + "%40"
                + operator
                + "&user_password="
                + password
                + "&wlan_user_ip="
                + localIPAddress
                + "&wlan_user_ipv6=&wlan_user_mac=000000000000&wlan_ac_ip=&wlan_ac_name=&jsVersion=3.3.3&v=7223";

        // 执行异步登录请求
        HttpUtil.getAsync(url, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() -> {
                    showError("网络请求失败：" + e.getMessage());
                    resetLoginButton();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String result = response.body() != null ? response.body().string() : null;
                requireActivity().runOnUiThread(() -> {
                    if (response.isSuccessful() && result != null) {
                        showSuccess(result);
                        // 解析登录结果
                        try {
                            String _result = result;
                            _result = _result.substring(7, _result.length() - 1);
                            JSONObject json = new JSONObject(_result);
                            String resultValue = json.getString("result");
                            if(resultValue.equals("1")) {
                                //登录成功
                                showSuccess("登录成功");
                            } else if(resultValue.equals("0")) {
                                //账号已登陆
                                showError("账号已登陆或未知错误");
                            } else {
                                //未知错误
                                showError("登录失败：未知错误");
                            }
                        } catch (JSONException e) {
                            showError("解析json错误");
                        }
                    } else {
                        showError("服务器响应错误");
                    }
                    resetLoginButton();
                });
            }
        });
    }

    private void resetLoginButton() {
        binding.loginButton.setEnabled(true);
        binding.loginButton.setText("登录");
    }

    private void showError(String message) {
        if (getContext() != null) {
            Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG)
                    .setAction("确定", v -> {})
                    .show();
        }
    }

    private void showSuccess(String message) {
        if (getContext() != null) {
            Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(getResources().getColor(R.color.success_green, null))
                    .show();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}