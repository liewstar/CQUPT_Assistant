package com.example.CQUPT.api;

public class NewsDetailResponse {
    private int code;
    private String msg;
    private NewsDetailData data;

    public int getCode() { return code; }
    public String getMsg() { return msg; }
    public NewsDetailData getData() { return data; }

    public static class NewsDetailData {
        private NewsContent content;
        
        public NewsContent getContent() { return content; }
    }

    public static class NewsContent {
        private String title;
        private String pub_time;
        private String publisher;
        private String issuer;
        private String content;
        private String[] attachments;
        private ScheduleData data;

        public String getTitle() { return title; }
        public String getPubTime() { return pub_time; }
        public String getPublisher() { return publisher; }
        public String getIssuer() { return issuer; }
        public String getContent() { return content; }
        public String[] getAttachments() { return attachments; }
        public ScheduleData getScheduleData() { return data; }
    }

    public static class ScheduleData {
        private String schedule_time;
        private String schedule_location;

        public String getScheduleTime() { return schedule_time; }
        public String getScheduleLocation() { return schedule_location; }
    }
}
