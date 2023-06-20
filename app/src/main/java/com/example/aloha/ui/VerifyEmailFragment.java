package com.example.aloha.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aloha.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class VerifyEmailFragment extends Fragment {
    private BottomNavigationView bottomNavigationView;

    public VerifyEmailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_verify_email, container, false);
        // Khởi tạo BottomNavigationView
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);

        // Hiển thị BottomNavigationBar nếu đang ở trang HomeFragment
        bottomNavigationView.setVisibility(View.GONE);
        return view;
    }
}