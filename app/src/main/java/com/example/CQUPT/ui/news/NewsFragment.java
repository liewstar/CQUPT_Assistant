package com.example.CQUPT.ui.news;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.CQUPT.R;
import com.example.CQUPT.api.NewsApiService;
import com.example.CQUPT.api.NewsResponse;
import com.example.CQUPT.api.AiSummaryResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment {

    private RecyclerView newsRecyclerView;
    private NewsAdapter newsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NewsApiService apiService;
    private int currentPage = 1;
    private static final int PAGE_SIZE = 12;
    private boolean isLoading = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_news, container, false);

        setupRetrofit();
        setupViews(root);
        setupRecyclerView();
        loadNews(true);

        // 添加周摘要按钮点击事件
        root.findViewById(R.id.btn_weekly_summary).setOnClickListener(v -> getWeeklySummary());

        return root;
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://8.137.36.93:7999/")  // 10.0.2.2 是Android模拟器访问本机的特殊IP
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(NewsApiService.class);
    }

    private void setupViews(View root) {
        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> loadNews(true));
        
        newsRecyclerView = root.findViewById(R.id.news_recycler_view);
    }

    private void setupRecyclerView() {
        newsAdapter = new NewsAdapter(new ArrayList<>());
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        newsRecyclerView.setAdapter(newsAdapter);

        newsAdapter.setOnItemClickListener(newsItem -> {
            Bundle args = new Bundle();
            args.putString("id", newsItem.getId());
            args.putString("title", newsItem.getTitle());
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_newsFragment_to_newsDetailFragment, args);
        });

        newsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0) {
                    loadNews(false);
                }
            }
        });
    }

    private void loadNews(boolean refresh) {
        if (isLoading) return;
        
        if (refresh) {
            currentPage = 1;
        }
        
        isLoading = true;
        apiService.getNewsList(1, currentPage, PAGE_SIZE).enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(@NonNull Call<NewsResponse> call, @NonNull Response<NewsResponse> response) {
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    NewsResponse newsResponse = response.body();
                    if (newsResponse.getCode() == 0) {
                        List<NewsItem> newsList = newsResponse.getData().getList();
                        if (refresh) {
                            newsAdapter.setNewsList(newsList);
                        } else {
                            newsAdapter.addNewsList(newsList);
                        }
                        currentPage++;
                    } else {
                        showError("获取新闻列表失败: " + newsResponse.getMsg());
                    }
                } else {
                    showError("服务器响应错误");
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable t) {
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                showError("网络请求失败: " + t.getMessage());
            }
        });
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void getWeeklySummary() {
        // 调用后端API获取周摘要
        apiService.getWeeklySummary().enqueue(new Callback<AiSummaryResponse>() {
            @Override
            public void onResponse(@NonNull Call<AiSummaryResponse> call, @NonNull Response<AiSummaryResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 0) {
                    String weeklySummary = response.body().getData().getSummary();
                    Bundle args = new Bundle();
                    args.putString("title", "本周新闻摘要");
                    args.putString("date", response.body().getData().getLastUpdated());
                    args.putString("content", weeklySummary);
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_newsFragment_to_newsDetailFragment, args);
                } else {
                    showError("获取周摘要失败: " + (response.body() != null ? response.body().getMsg() : "未知错误"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AiSummaryResponse> call, @NonNull Throwable t) {
                showError("网络请求失败: " + t.getMessage());
            }
        });
    }
}