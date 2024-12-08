package com.example.CQUPT.api;

public class AiSummaryResponse {
    private int code;
    private String msg;
    private AiSummaryData data;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public AiSummaryData getData() {
        return data;
    }

    public static class AiSummaryData {
        private String summary;
        private String last_updated;

        public String getSummary() {
            return summary;
        }

        public String getLastUpdated() {
            return last_updated;
        }
    }
}
