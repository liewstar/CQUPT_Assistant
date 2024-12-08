package com.example.CQUPT.ui.news;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.CQUPT.R;
import com.example.CQUPT.api.NewsApiService;
import com.example.CQUPT.api.NewsDetailResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsDetailFragment extends Fragment {
    private static final String TAG = "NewsDetailFragment";
    private TextView titleTextView;
    private TextView timeTextView;
    private TextView publisherTextView;
    private WebView contentWebView;
    private ProgressBar progressBar;
    private NewsApiService apiService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_news_detail, container, false);
        
        setupViews(root);
        setupRetrofit();
        loadNewsDetail();
        
        return root;
    }

    private void setupViews(View root) {
        titleTextView = root.findViewById(R.id.news_detail_title);
        timeTextView = root.findViewById(R.id.news_detail_time);
        publisherTextView = root.findViewById(R.id.news_detail_publisher);
        contentWebView = root.findViewById(R.id.news_content_webview);
        progressBar = root.findViewById(R.id.progress_bar);

        // 配置WebView
        contentWebView.getSettings().setJavaScriptEnabled(true);
        contentWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d(TAG, "WebView Console: " + consoleMessage.message());
                return true;
            }
        });
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://8.137.36.93:7999/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(NewsApiService.class);
    }

    private void loadNewsDetail() {
        if (getArguments() == null) return;
        
        String newsId = getArguments().getString("id");
        String aiSummaryContent = getArguments().getString("content");

        // 如果是AI摘要，直接显示
        if (aiSummaryContent != null && !aiSummaryContent.isEmpty()) {
            titleTextView.setText("本周新闻摘要");
            timeTextView.setText(getArguments().getString("date", ""));
            publisherTextView.setText("AI生成");

            String markdownContent = aiSummaryContent;
            String htmlContent = String.format(
                    "<html>" +
                            "<head>" +
                            "<meta name='viewport' content='width=device-width, initial-scale=1'>" +
                            "<link rel='stylesheet' href='https://cdn.jsdelivr.net/npm/github-markdown-css@5.2.0/github-markdown.min.css'>" +
                            "<script src='https://cdn.jsdelivr.net/npm/marked/marked.min.js'></script>" +
                            "<style>" +
                            "body { padding: 16px; }" +
                            ".markdown-body { box-sizing: border-box; min-width: 200px; max-width: 100%%; margin: 0 auto; padding: 15px; }" +
                            "</style>" +
                            "</head>" +
                            "<body class='markdown-body'>" +
                            "<div id='content'></div>" +
                            "<script>" +
                            "document.getElementById('content').innerHTML = marked.parse(`%s`);" +
                            "</script>" +
                            "</body>" +
                            "</html>",
                    markdownContent.replace("`", "\\`").replace("$", "\\$")
            );

            contentWebView.loadDataWithBaseURL(
                    "https://example.com",
                    htmlContent,
                    "text/html",
                    "UTF-8",
                    null
            );
            progressBar.setVisibility(View.GONE);
            return;
        }

        // 如果不是AI摘要，走原来的逻辑
        if (newsId == null) return;

        progressBar.setVisibility(View.VISIBLE);
        apiService.getNewsDetail(newsId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<NewsDetailResponse> call, @NonNull Response<NewsDetailResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    NewsDetailResponse detailResponse = response.body();
                    if (detailResponse.getCode() == 0) {
                        NewsDetailResponse.NewsContent content = detailResponse.getData().getContent();
                        titleTextView.setText(content.getTitle());
                        timeTextView.setText(content.getPubTime());
                        String publisher = content.getPublisher();
                        if (content.getIssuer() != null && !content.getIssuer().isEmpty()) {
                            publisher += " " + content.getIssuer();
                        }
                        publisherTextView.setText(publisher);

                        String markdownContent = content.getContent();
                        Log.d(TAG, "Markdown Content: " + markdownContent);

                        // 使用WebView加载Markdown内容
                        String htmlContent = String.format(
                                "<html>" +
                                        "<head>" +
                                        "<meta name='viewport' content='width=device-width, initial-scale=1'>" +
                                        "<link rel='stylesheet' href='https://cdn.jsdelivr.net/npm/github-markdown-css@5.2.0/github-markdown.min.css'>" +
                                        "<script src='https://cdn.jsdelivr.net/npm/marked/marked.min.js'></script>" +
                                        "<style>" +
                                        "body { padding: 16px; }" +
                                        ".markdown-body { box-sizing: border-box; min-width: 200px; max-width: 100%%; margin: 0 auto; padding: 15px; }" +
                                        "</style>" +
                                        "</head>" +
                                        "<body class='markdown-body'>" +
                                        "<div id='content'></div>" +
                                        "<script>" +
                                        "console.log('Markdown content:', `%s`);" +
                                        "document.getElementById('content').innerHTML = marked.parse(`%s`);" +
                                        "console.log('Parsed HTML:', document.getElementById('content').innerHTML);" +
                                        "</script>" +
                                        "</body>" +
                                        "</html>",
                                markdownContent.replace("`", "\\`").replace("$", "\\$"),
                                markdownContent.replace("`", "\\`").replace("$", "\\$")
                        );

                        Log.d(TAG, "HTML Content: " + htmlContent);

                        contentWebView.loadDataWithBaseURL(
                                "https://example.com",  // 添加一个基础URL
                                htmlContent,
                                "text/html",
                                "UTF-8",
                                null
                        );
                    } else {
                        showError("获取新闻详情失败: " + detailResponse.getMsg());
                    }
                } else {
                    showError("服务器响应错误");
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewsDetailResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                showError("网络请求失败: " + t.getMessage());
                Log.e(TAG, "Network error", t);
            }
        });
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
        Log.e(TAG, "Error: " + message);
    }
}