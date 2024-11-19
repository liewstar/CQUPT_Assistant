package com.example.CQUPT.ui.news;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.CQUPT.R;

public class NewsDetailFragment extends Fragment {
    private static final String ARG_TITLE = "title";
    private static final String ARG_DATE = "date";
    private static final String ARG_CONTENT = "content";

    public static NewsDetailFragment newInstance(String title, String date, String content) {
        NewsDetailFragment fragment = new NewsDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DATE, date);
        args.putString(ARG_CONTENT, content);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_news_detail, container, false);

        TextView titleTextView = root.findViewById(R.id.news_detail_title);
        TextView dateTextView = root.findViewById(R.id.news_detail_date);
        TextView contentTextView = root.findViewById(R.id.news_detail_content);

        if (getArguments() != null) {
            titleTextView.setText(getArguments().getString(ARG_TITLE));
            dateTextView.setText(getArguments().getString(ARG_DATE));
            contentTextView.setText(getArguments().getString(ARG_CONTENT));
        }

        return root;
    }
}