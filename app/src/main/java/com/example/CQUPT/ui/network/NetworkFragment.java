package com.example.CQUPT.ui.network;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.CQUPT.R;
import com.example.CQUPT.databinding.FragmentLoginBinding;

public class NetworkFragment extends Fragment {

    private FragmentLoginBinding binding;

    public static NetworkFragment newInstance() {
        return new NetworkFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        TextView textView = view.findViewById(R.id.text);
//        textView.setText("测试进入碎片" + System.currentTimeMillis());

    }
}