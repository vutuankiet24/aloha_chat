package com.example.aloha.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aloha.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SplashFragment extends Fragment {
    private BottomNavigationView bottomNavigationView;
    public SplashFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_splash, container, false);
        View view = inflater.inflate(R.layout.fragment_splash, container, false);

        // Khởi tạo BottomNavigationView
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);

        // Hiển thị BottomNavigationBar nếu đang ở trang HomeFragment
        bottomNavigationView.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Sử dụng Handler để đợi trong 10 giây, sau đó chuyển sang Fragment đăng nhập
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Sử dụng FragmentManager để chuyển sang Fragment đăng nhập
                FragmentManager fragmentManager = getFragmentManager();
                assert fragmentManager != null;
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new LoginFragment());

                fragmentTransaction.commit();
            }
        }, 1000); // 5 giây
    }
}