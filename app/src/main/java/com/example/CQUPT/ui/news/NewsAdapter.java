package com.example.CQUPT.ui.news;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.CQUPT.R;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private List<NewsItem> newsList;
    private OnItemClickListener listener;

    public NewsAdapter(List<NewsItem> newsList) {
        this.newsList = newsList;
    }

    public void setNewsList(List<NewsItem> newsList) {
        this.newsList = newsList;
        notifyDataSetChanged();
    }

    public void addNewsList(List<NewsItem> moreNews) {
        int startPos = newsList.size();
        newsList.addAll(moreNews);
        notifyItemRangeInserted(startPos, moreNews.size());
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem newsItem = newsList.get(position);
        holder.titleTextView.setText(newsItem.getTitle());
        holder.dateTextView.setText(newsItem.getDate());
        holder.readNumsTextView.setText(String.format("阅读量: %d", newsItem.getReadNums()));
        
        // 设置已读/未读状态
        holder.itemView.setAlpha(newsItem.isRead() ? 0.7f : 1.0f);
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                newsItem.setRead(true);
                notifyItemChanged(position);
                listener.onItemClick(newsItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(NewsItem newsItem);
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView dateTextView;
        TextView readNumsTextView;

        NewsViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.news_title);
            dateTextView = itemView.findViewById(R.id.news_date);
            readNumsTextView = itemView.findViewById(R.id.news_read_nums);
        }
    }
}