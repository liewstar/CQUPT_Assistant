package com.example.CQUPT.ui.news;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.CQUPT.R;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment {

    private RecyclerView newsRecyclerView;
    private NewsAdapter newsAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_news, container, false);

        newsRecyclerView = root.findViewById(R.id.news_recycler_view);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<NewsItem> newsList = getNewsList();
        newsAdapter = new NewsAdapter(newsList);
        newsRecyclerView.setAdapter(newsAdapter);

        newsAdapter.setOnItemClickListener(newsItem -> {
            NewsDetailFragment detailFragment = NewsDetailFragment.newInstance(
                    newsItem.getTitle(),
                    newsItem.getDate(),
                    newsItem.getContent()
            );
            Navigation.findNavController(root).navigate(R.id.action_newsFragment_to_newsDetailFragment, detailFragment.getArguments());
        });

        // 添加周摘要按钮点击事件
        root.findViewById(R.id.btn_weekly_summary).setOnClickListener(v -> {
            // 这里应该调用后端API获取周摘要
            String weeklySummary = getWeeklySummary();
            Bundle args = new Bundle();
            args.putString("title", "本周新闻摘要");
            args.putString("date", ""); // 可以根据需要设置日期
            args.putString("content", weeklySummary);
            Navigation.findNavController(root).navigate(R.id.action_newsFragment_to_newsDetailFragment, args);
        });

        return root;
    }

    private List<NewsItem> getNewsList() {
        List<NewsItem> newsList = new ArrayList<>();
        newsList.add(new NewsItem("重庆邮电大学举办2023年度学术交流会", "2023-05-15", "详细内容..."));
        newsList.add(new NewsItem("我校学生在全国大学生创新创业大赛中获得金奖", "2023-05-14", "详细内容..."));
        newsList.add(new NewsItem("重邮与华为签署战略合作协议", "2023-05-13", "详细内容..."));
        newsList.add(new NewsItem("2023年重庆邮电大学招生简章发布", "2023-05-12", "详细内容..."));
        newsList.add(new NewsItem("重邮计算机学院举办人工智能论坛", "2023-05-11", "详细内容..."));
        return newsList;
    }

    private String getWeeklySummary() {
        // 这里应该是调用后端API获取周摘要的逻辑
        // 为了演示，这里返回一个模拟的摘要
        return "这是本周新闻的AI摘要。包含了重要事件1、重要事件2、重要事件3等内容...";
    }
}