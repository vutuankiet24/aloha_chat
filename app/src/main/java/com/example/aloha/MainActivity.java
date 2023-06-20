package com.example.aloha;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.aloha.listeners.OnBackPressedListener;
import com.example.aloha.ui.AddFriendFragment;
import com.example.aloha.ui.ChatFragment;
import com.example.aloha.ui.FriendInviteFragment;
import com.example.aloha.ui.ListFriendFragment;
import com.example.aloha.ui.ProfileFragment;
import com.example.aloha.ui.ChatsFragment;
import com.example.aloha.ui.LoginFragment;
import com.example.aloha.ui.SignUpFragment;
import com.example.aloha.ui.SplashFragment;
import com.example.aloha.utilities.Constants;
import com.example.aloha.utilities.PreferenceManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

//public class MainActivity extends AppCompatActivity {
//
//    private BottomNavigationView bottomNavigationView;
//    private SplashFragment splashFragment;
//    private HomeFragment homeFragment;
//    private LoginFragment loginFragment;
//    private SignUpFragment signUpFragment;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        bottomNavigationView = findViewById(R.id.bottom_navigation);
//        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
//            switch (item.getItemId()) {
//                case R.id.navigation_home:
//                    showFragment(homeFragment);
//                    return true;
//                case R.id.navigation_account:
//                    showFragment(loginFragment);
//                    return true;
//                case R.id.navigation_settings:
//                    showFragment(signUpFragment);
//                    return true;
//            }
//            return false;
//        });
//
//        splashFragment = new SplashFragment();
//        homeFragment = new HomeFragment();
//        loginFragment = new LoginFragment();
//        signUpFragment = new SignUpFragment();
//
//        // Hiển thị fragment home mặc định khi khởi động
//        showFragment(splashFragment);
//    }
//
//    private void showFragment(Fragment fragment) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.fragment_container, fragment);
//
//
//        if (fragment instanceof SplashFragment) {
//            bottomNavigationView.setVisibility(View.VISIBLE);
//        } else {
//            bottomNavigationView.setVisibility(View.GONE);
//        }
//        transaction.commit();
//    }
//}

public class MainActivity extends BaseActivity implements OnBackPressedListener{

    private PreferenceManager preferenceManager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferenceManager = new PreferenceManager(getApplication());

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_chats:
                    // Thực hiện chuyển đổi đến HomeFragment
                    ChatsFragment chatsFragment = new ChatsFragment();
                    fragmentTransaction.replace(R.id.fragment_container, chatsFragment);
                    break;
                case R.id.navigation_add_user:
                    // Thực hiện chuyển đổi đến HomeFragment
                    AddFriendFragment addFriendFragment = new AddFriendFragment();
                    fragmentTransaction.replace(R.id.fragment_container, addFriendFragment);
                    break;
                case R.id.navigation_user_invite:
                    // Thực hiện chuyển đổi đến HomeFragment
                    FriendInviteFragment friendInviteFragment = new FriendInviteFragment();
                    fragmentTransaction.replace(R.id.fragment_container, friendInviteFragment);
                    break;
                case R.id.navigation_list_user:
                    // Thực hiện chuyển đổi đến HomeFragment
                    ListFriendFragment listFriendFragment = new ListFriendFragment();
                    fragmentTransaction.replace(R.id.fragment_container, listFriendFragment);
                    break;
                case R.id.navigation_profile:
                    // Thực hiện chuyển đổi đến HomeFragment
                    ProfileFragment profileFragment = new ProfileFragment();
                    fragmentTransaction.replace(R.id.fragment_container, profileFragment);
                    break;
            }

            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

            getToken();

            return true;
        });
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        int backStackEntryCount = fragmentManager.getBackStackEntryCount();
        if (currentFragment instanceof ChatsFragment) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_chats);
            Log.d("TAG_log_a", "onBackPressed: " + backStackEntryCount);
            finish();
        } else if (currentFragment instanceof ProfileFragment) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
            Log.d("TAG_log_b", "onBackPressed: " + backStackEntryCount);
            finish();
        } else if (currentFragment instanceof ListFriendFragment) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_list_user);
            Log.d("TAG_log_b", "onBackPressed: " + backStackEntryCount);
            finish();
        } else if (currentFragment instanceof AddFriendFragment) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_add_user);
            Log.d("TAG_log_b", "onBackPressed: " + backStackEntryCount);
            finish();
        } else if (currentFragment instanceof FriendInviteFragment) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_user_invite);
            Log.d("TAG_log_b", "onBackPressed: " + backStackEntryCount);
            finish();
        } else if (currentFragment instanceof ChatFragment) {
            Log.d("TAG_log_chat", "onBackPressed: " + backStackEntryCount);
            ChatsFragment chatsFragment = new ChatsFragment();

            // Thực hiện chuyển đổi Fragment
            assert fragmentManager != null;
            fragmentTransaction.replace(R.id.fragment_container, chatsFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

            // Hiển thị BottomNavigationBar nếu đang ở trang HomeFragment
            bottomNavigationView.setVisibility(View.VISIBLE);
            // Thiết lập biểu tượng hoạt động (active icon) cho mục đang chọn
            bottomNavigationView.getMenu().findItem(R.id.navigation_chats).setChecked(true);
        } else {
            super.onBackPressed();
        }

        Log.d("TAG_log_c", "onBackPressed: " + backStackEntryCount);
    }

    private void showToast(String message){
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show();
    }

    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.KEY_ALOHA_TOKEN, token)
                .addOnSuccessListener(unused -> showToast("Token update successfully"))
                .addOnFailureListener(e -> showToast("Unable to update token"));
    }
}

