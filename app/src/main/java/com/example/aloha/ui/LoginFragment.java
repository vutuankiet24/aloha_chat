package com.example.aloha.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aloha.R;
import com.example.aloha.utilities.Constants;
import com.example.aloha.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class LoginFragment extends Fragment {
    TextView mTextViewSignUp, mTextForgotPassword;
    private BottomNavigationView bottomNavigationView;
    EditText mEmail, mPassword;

    Button mBtnSignIn;
    ProgressBar mProgressBar;
    boolean passwordVisible;

    private PreferenceManager preferenceManager;
    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        preferenceManager = new PreferenceManager(getContext());
        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            navigateToHomeFragment();
        }

        // Tìm kiếm TextView để click
        mTextViewSignUp = view.findViewById(R.id.textViewSignUp);
        mTextForgotPassword = view.findViewById(R.id.textViewForgotPassword);
        mEmail = view.findViewById(R.id.editTextEmailAddress);
        mPassword = view.findViewById(R.id.editTextPassword);
        mBtnSignIn = view.findViewById(R.id.btnLogin);
        mProgressBar = view.findViewById(R.id.progressBarLogin);
        bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);

        mPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int Right=2;
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    if(motionEvent.getRawX() >= mPassword.getRight()-mPassword.getCompoundDrawables()[Right].getBounds().width()){
                        int selection = mPassword.getSelectionEnd();
                        if(passwordVisible){
                            // set srawble image here
                            mPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_lock_24,0,R.drawable.baseline_visibility_off_24,0);
                            mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        }else {
                            // set srawble image here
                            mPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_lock_open_24,0,R.drawable.baseline_visibility_24,0);
                            mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                        }

                        mPassword.setSelection(selection);
                        return true;
                    }
                }

                return false;
            }
        });

        // Gắn OnClickListener cho TextView
        mTextViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo một đối tượng SignUpFragment mới
                SignUpFragment signUpFragment = new SignUpFragment();

                // Thực hiện chuyển đổi Fragment
                FragmentManager fragmentManager = getFragmentManager();
                assert fragmentManager != null;
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, signUpFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        // Gắn OnClickListener cho TextView
        mTextForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo một đối tượng SignUpFragment mới
                ForgotPasswordFragment forgotPasswordFragment = new ForgotPasswordFragment();

                // Thực hiện chuyển đổi Fragment
                FragmentManager fragmentManager = getFragmentManager();
                assert fragmentManager != null;
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, forgotPasswordFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        // Gắn OnClickListener cho Button
        mBtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thực hiện chuyển đổi Fragment
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//                // Tạo một đối tượng HomeFragment mới
//                ChatsFragment homeFragment = new ChatsFragment();
//
//                // Thay đổi fragment_container bằng homeFragment
//                fragmentTransaction.replace(R.id.fragment_container, homeFragment);
//                fragmentTransaction.addToBackStack(null);
//
//                // Hiển thị Bottom Navigation Bar
//                bottomNavigationView.setSelectedItemId(R.id.navigation_chats);
//
//                bottomNavigationView.setVisibility(View.VISIBLE);
//
//                fragmentTransaction.commit();

//                navigateToHomeFragment();
                if(isValidSignInDetails()){
                    signIn();
                }
            }
        });

        // Khởi tạo BottomNavigationView
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);

        // Hiển thị BottomNavigationBar nếu đang ở trang HomeFragment
        bottomNavigationView.setVisibility(View.GONE);
        return view;
    }

    private void navigateToHomeFragment() {
        // Lấy FragmentManager và FragmentTransaction
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Tạo một đối tượng HomeFragment mới
        ChatsFragment chatsFragment = new ChatsFragment();

        // Thay thế fragment hiện tại bằng HomeFragment
        fragmentTransaction.replace(R.id.fragment_container, chatsFragment);


        // Thêm fragment vào back stack
        fragmentTransaction.addToBackStack(null);

        // Hoàn thành giao dịch và xác nhận thay đổi
        fragmentTransaction.commit();
    }

    private void addDataToFirestore(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String,Object> data = new HashMap<>();
        data.put("firstName","Vu");
        data.put("lastName","Tuan Kiet");
        database.collection("users")
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Data insert", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Xử lý lỗi ở đây
                // Ví dụ: hiển thị thông báo lỗi cho người dùng
                Toast.makeText(getActivity(), "Đã xảy ra lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }});
    }

    public void signIn() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, mEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, mPassword.getText().toString())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        if (!querySnapshot.isEmpty() && querySnapshot.getDocuments().size() > 0) {
                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                            preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                            preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                            Log.d("TAG_name", "onSuccess: "+documentSnapshot.getString(Constants.KEY_NAME));
                            preferenceManager.putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL));
                            preferenceManager.putString(Constants.KEY_PASSWORD, documentSnapshot.getString(Constants.KEY_PASSWORD));
                            preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));

                            navigateToHomeFragment();

                        } else {
                            loading(false);
                            showToast("Unable to sign in");
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loading(false);
                        Toast.makeText(getActivity(), "Đã xảy ra lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void showToast(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
    private boolean isValidSignInDetails(){
        if (mEmail.getText().toString().trim().isEmpty()) {
            showToast("Enter Email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(mEmail.getText().toString()).matches()) {
            showToast("Enter valid Email");
            return false;
        } else if (mPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter Password");
            return false;
        } else {
            return true;
        }
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            mBtnSignIn.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mBtnSignIn.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }


}