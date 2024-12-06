package com.example.CQUPT.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NewsApiService {
    @GET("api/news")
    Call<NewsResponse> getNewsList(
        @Query("types") int types,
        @Query("page") int page,
        @Query("limit") int limit
    );

    @GET("api/news/{id}")
    Call<NewsDetailResponse> getNewsDetail(@Path("id") String id);
}
