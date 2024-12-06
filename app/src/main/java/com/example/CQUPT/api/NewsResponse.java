package com.example.CQUPT.api;

import com.example.CQUPT.ui.news.NewsItem;
import java.util.List;

public class NewsResponse {
    private int code;
    private String msg;
    private NewsData data;

    public int getCode() { return code; }
    public String getMsg() { return msg; }
    public NewsData getData() { return data; }

    public static class NewsData {
        private List<NewsItem> list;
        
        public List<NewsItem> getList() { return list; }
    }
}
