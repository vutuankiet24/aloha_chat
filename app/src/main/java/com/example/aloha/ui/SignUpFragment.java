package com.example.aloha.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Random;

public class SignUpFragment extends Fragment {

    TextView mTextViewLogin;

    EditText mEmail, mPassword, mConfirmPassword;
    Button mBtnSignUp;
    ProgressBar mProgressBar;

    boolean passwordVisible, confirmPasswordVisible;
    private BottomNavigationView bottomNavigationView;
    private String encodedImage;

    private PreferenceManager preferenceManager;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        preferenceManager = new PreferenceManager(getContext());

        // Tìm kiếm TextView để click
        mTextViewLogin = view.findViewById(R.id.textViewLogin);
        mBtnSignUp = view.findViewById(R.id.btnSignUp);
        mProgressBar = view.findViewById(R.id.progressBar);

        mEmail = view.findViewById(R.id.editTextEmailAddress);
        mPassword = view.findViewById(R.id.editTextPassword);
        mConfirmPassword = view.findViewById(R.id.editTextConfirmPassword);

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

        mConfirmPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int Right=2;
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    if(motionEvent.getRawX() >= mConfirmPassword.getRight()-mConfirmPassword.getCompoundDrawables()[Right].getBounds().width()){
                        int selection = mConfirmPassword.getSelectionEnd();
                        if(confirmPasswordVisible){
                            // set srawble image here
                            mConfirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_lock_reset_24,0,R.drawable.baseline_visibility_off_24,0);
                            mConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            confirmPasswordVisible = false;
                        }else {
                            // set srawble image here
                            mConfirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_lock_reset_24,0,R.drawable.baseline_visibility_24,0);
                            mConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            confirmPasswordVisible = true;
                        }

                        mConfirmPassword.setSelection(selection);
                        return true;
                    }
                }

                return false;
            }
        });

        // Gắn OnClickListener cho TextView
        mTextViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo một đối tượng SignUpFragment mới
                LoginFragment loginFragment = new LoginFragment();

                // Thực hiện chuyển đổi Fragment
                FragmentManager fragmentManager = getFragmentManager();
                assert fragmentManager != null;
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, loginFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(isValidSignUpDetails()){
                    signUp();
                }
            }
        });

        // Khởi tạo BottomNavigationView
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);

        // Hiển thị BottomNavigationBar nếu đang ở trang HomeFragment
        bottomNavigationView.setVisibility(View.GONE);
        return view;
    }

    private String randomAvatar(){
        String[] avatar = {
                "https://firebasestorage.googleapis.com/v0/b/k34dl-8e937.appspot.com/o/quickdraw-default.png?alt=media&token=3e24e12b-99d1-44d7-a0a8-e5b4f67a5284",
                "https://firebasestorage.googleapis.com/v0/b/k34dl-8e937.appspot.com/o/75593060.png?alt=media&token=eb6d943e-b849-4c61-9bfc-f3a5ac0197e9",
                "https://firebasestorage.googleapis.com/v0/b/k34dl-8e937.appspot.com/o/pull-shark-default.png?alt=media&token=d8a867e5-8560-4b47-892b-01511d4b82b9",
                "https://firebasestorage.googleapis.com/v0/b/k34dl-8e937.appspot.com/o/1.png?alt=media&token=fba4d923-b4f7-4681-8704-3cb53260ede2",
                "https://firebasestorage.googleapis.com/v0/b/k34dl-8e937.appspot.com/o/2.png?alt=media&token=1b86cbf8-9616-4cd2-9381-d99aff22736b",
                "https://firebasestorage.googleapis.com/v0/b/k34dl-8e937.appspot.com/o/3.png?alt=media&token=6fffe950-4ced-475f-a11a-5945854435a5",
                "https://firebasestorage.googleapis.com/v0/b/k34dl-8e937.appspot.com/o/4.png?alt=media&token=408b465a-b038-4806-ba9a-32dfa0dc3d06",
                "https://firebasestorage.googleapis.com/v0/b/k34dl-8e937.appspot.com/o/5.png?alt=media&token=a08475a1-b557-4453-af45-7b0878f577c1",
                "https://firebasestorage.googleapis.com/v0/b/k34dl-8e937.appspot.com/o/6.png?alt=media&token=1c7a2434-b521-47fe-acf4-358657acb7ea",
                "https://firebasestorage.googleapis.com/v0/b/k34dl-8e937.appspot.com/o/7.png?alt=media&token=1820f2f9-f4fa-4b3b-96b8-acd92cce8216",
        };
//            Random random = new Random();
//            int index = random.nextInt(avatar.length);
        int index = new Random().nextInt(avatar.length);
        String random = (avatar[index]);
        return random;
    }

    private void signUp(){
        String image = randomAvatar();
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String,Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME, mEmail.getText().toString());
        user.put(Constants.KEY_EMAIL, mEmail.getText().toString());
        user.put(Constants.KEY_PASSWORD, mPassword.getText().toString());
        user.put(Constants.KEY_IMAGE, image);
        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.KEY_USER_ID , documentReference.getId());
                    preferenceManager.putString(Constants.KEY_EMAIL , mEmail.getText().toString());
                    preferenceManager.putString(Constants.KEY_NAME , mEmail.getText().toString());
                    preferenceManager.putString(Constants.KEY_IMAGE, image);


                    // Tạo một đối tượng SignUpFragment mới
                    ActiveFragment activeFragment = new ActiveFragment();

                    // Thực hiện chuyển đổi Fragment
                    FragmentManager fragmentManager = getFragmentManager();
                    assert fragmentManager != null;
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, activeFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xử lý lỗi ở đây
                        loading(false);
                        // Ví dụ: hiển thị thông báo lỗi cho người dùng
                        Toast.makeText(getActivity(), "Đã xảy ra lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }});
    }

    private void showToast(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
    private boolean isValidSignUpDetails(){
        if (mEmail.getText().toString().trim().isEmpty()) {
            showToast("Enter Email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(mEmail.getText().toString()).matches()) {
            showToast("Enter valid Email");
            return false;
        } else if (mPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter Password");
            return false;
        } else if (mConfirmPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter Confirm Password");
            return false;
        } else if (!mPassword.getText().toString().equals(mConfirmPassword.getText().toString())) {
            showToast("Password and Confirm Password must be same");
            return false;
        } else {
            return true;
        }
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            mBtnSignUp.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mBtnSignUp.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }
}