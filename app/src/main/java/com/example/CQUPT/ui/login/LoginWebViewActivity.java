package com.example.CQUPT.ui.login;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.webkit.*;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class LoginWebViewActivity extends AppCompatActivity {
    private WebView webView;
    private static final String PREF_SESSION_ID = "session_id";
    private boolean hasValidSession = false;
    private static final String TAG = "LoginWebViewActivity";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        webView = new WebView(this);
        setContentView(webView);

        setupWebView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG, "Page started loading: " + url);
                Toast.makeText(LoginWebViewActivity.this, "页面加载中...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                Log.d(TAG, "URL changed to: " + url);
                checkForValidSession(url);
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "Page finished loading: " + url);
                checkForValidSession(url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e(TAG, "Error loading page: " + error.getDescription());
                Toast.makeText(LoginWebViewActivity.this, "页面加载错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // 处理SSL证书问题
            }
        });

        // 加载教务系统登录页面
        webView.loadUrl("http://jwzx.cqupt.edu.cn/login.php");
    }

    private void checkForValidSession(String url) {
        if (hasValidSession) return;

        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(url);
        
        if (cookies != null && url.contains("localhost")) {
            Log.d(TAG, "Found cookies: " + cookies);
            for (String cookie : cookies.split(";")) {
                cookie = cookie.trim();
                if (cookie.startsWith("PHPSESSID=")) {
                    String sessionId = cookie.substring("PHPSESSID=".length());
                    Log.d(TAG, "Found valid session ID: " + sessionId);
                    saveSessionId(sessionId);
                    hasValidSession = true;
                    Toast.makeText(LoginWebViewActivity.this, 
                        "Session获取成功，已保存", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
            }
        }
    }

    private void saveSessionId(String sessionId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString(PREF_SESSION_ID, sessionId).apply();
    }

    @Override
    public void onBackPressed() {
        if (!hasValidSession) {
            Toast.makeText(this, "请先登录获取Session", Toast.LENGTH_SHORT).show();
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        webView.destroy();
        super.onDestroy();
    }
}
