package com.example.aloha.ui;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.aloha.R;
import com.example.aloha.databinding.FragmentChatsBinding;
import com.example.aloha.databinding.FragmentProfileBinding;
import com.example.aloha.utilities.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class ProfileFragment extends Fragment {
    private BottomNavigationView bottomNavigationView;
    Toolbar mToolbar;
    TextView mUserName;
    Button mBtnLogout;
    ImageView mImageView;
    FragmentProfileBinding binding;
    private com.example.aloha.utilities.PreferenceManager preferenceManager;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        preferenceManager = new com.example.aloha.utilities.PreferenceManager(getContext());


        mUserName = view.findViewById(R.id.name);
        mBtnLogout = view.findViewById(R.id.btnLogout);
        mImageView = view.findViewById(R.id.imageView);
        mToolbar = view.findViewById(R.id.profile_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setTitle("Profile");

        // Khởi tạo BottomNavigationView
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
        // Hiển thị BottomNavigationBar nếu đang ở trang HomeFragment
        bottomNavigationView.setVisibility(View.VISIBLE);

        loadUserDetails();


        // Gắn OnClickListener cho Button
        mBtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Khôi phục trạng thái Night Mode
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean isNightMode = sharedPreferences.getBoolean("night_mode", false);

        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);

        int nightMode = AppCompatDelegate.getDefaultNightMode();
        if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            menu.findItem(R.id.navigation_night).setIcon(R.drawable.ic_light);
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            mToolbar.setBackgroundColor(ContextCompat.getColor(activity, R.color.black));
            bottomNavigationView.setBackgroundColor(ContextCompat.getColor(activity, R.color.black));
        } else {
            menu.findItem(R.id.navigation_night).setIcon(R.drawable.ic_night);
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            mToolbar.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
            bottomNavigationView.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
        }

        // Lưu giá trị màu nền hiện tại vào SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("dialog_background", nightMode == AppCompatDelegate.MODE_NIGHT_YES ? R.color.dialog_background_dark : R.color.dialog_background_light);
        editor.apply();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.navigation_night) {
            // Xử lý khi chọn Night Mode

            int nightMode = AppCompatDelegate.getDefaultNightMode();
            if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                // Lưu trạng thái Light Mode
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("night_mode", false);
                editor.apply();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                // Lưu trạng thái Night Mode
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("night_mode", true);
                editor.apply();
            }
            getActivity().recreate(); // Gọi recreate() từ Activity chứa Fragment

            return true;
        } else if (itemId == R.id.navigation_log_out
        ) {
            // Xử lý khi chọn Log Out
            showConfirmationDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(R.layout.dialog_confirm);

        AlertDialog dialog = builder.create();

        // Lấy giá trị màu nền từ SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int dialogBackground = sharedPreferences.getInt("dialog_background", R.color.dialog_background_light);

        // Thiết lập màu nền cho dialog
        dialog.getWindow().setBackgroundDrawableResource(dialogBackground);

        dialog.show();

        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            // Thực hiện các thao tác cần thiết để thoát ứng dụng
            requireActivity().finish();
        });
    }

    private void loadUserDetails(){
        String imageUrl = preferenceManager.getString(Constants.KEY_IMAGE);

        Glide.with(this)
                .load(imageUrl)
                .into(mImageView);
        mUserName.setText(preferenceManager.getString(Constants.KEY_NAME));
        Log.d("TAG_AVAILABILITY", "loadUserDetails: " + preferenceManager.getString(Constants.KEY_AVAILABILITY));
        long availability = Long.parseLong(preferenceManager.getString(Constants.KEY_AVAILABILITY));
        if(availability == 1){
            binding.dotAvailability.setVisibility(View.VISIBLE);
        } else if(availability == 0){
            binding.dotAvailability.setVisibility(View.GONE);
        }
//        mImageView.setImageURI();
    }

    private void showToast(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void signOut(){
        Toast.makeText(getContext(), "Signing Out ...", Toast.LENGTH_SHORT).show();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_ALOHA_TOKEN, FieldValue.delete());
        documentReference.update((updates))
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();

                    // Sử dụng FragmentManager để chuyển sang Fragment đăng nhập
                    FragmentManager fragmentManager = getFragmentManager();
                    assert fragmentManager != null;
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, new LoginFragment());

                    fragmentTransaction.commit();
                })
                .addOnFailureListener(e -> showToast("Unable to sign out"));
    }
}