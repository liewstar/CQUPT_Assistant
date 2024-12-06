package com.example.CQUPT.ui.news;

public class NewsItem {
    private String id;
    private int type;
    private String type_id;
    private String title;
    private String date;
    private boolean is_read;
    private int read_nums;

    public NewsItem(String id, int type, String type_id, String title, 
                   String date, boolean is_read, int read_nums) {
        this.id = id;
        this.type = type;
        this.type_id = type_id;
        this.title = title;
        this.date = date;
        this.is_read = is_read;
        this.read_nums = read_nums;
    }

    public String getId() { return id; }
    public int getType() { return type; }
    public String getTypeId() { return type_id; }
    public String getTitle() { return title; }
    public String getDate() { return date; }
    public boolean isRead() { return is_read; }
    public int getReadNums() { return read_nums; }

    public void setRead(boolean read) { this.is_read = read; }
}