package com.example.aloha.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.aloha.R;
import com.example.aloha.adapters.RecentConversationsAdapter;
import com.example.aloha.adapters.UsersAdapter;
import com.example.aloha.databinding.FragmentAddFriendBinding;
import com.example.aloha.listeners.OnBackPressedListener;
import com.example.aloha.listeners.UserListener;
import com.example.aloha.models.Friend;
import com.example.aloha.models.User;
import com.example.aloha.utilities.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AddFriendFragment extends Fragment implements UserListener {
    private FragmentAddFriendBinding binding;
    private BottomNavigationView bottomNavigationView;
    Toolbar mToolbar;
    SearchView searchView;
    private String friendId = null;
    private com.example.aloha.utilities.PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private Activity activity;
    private UsersAdapter usersAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
    }
    public AddFriendFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_add_friend, container, false);

//        View view = inflater.inflate(R.layout.fragment_add_friend, container, false);

        binding = FragmentAddFriendBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        preferenceManager = new com.example.aloha.utilities.PreferenceManager(getContext());

        mToolbar = view.findViewById(R.id.add_friend_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setTitle("Add Friend");

        // Khởi tạo BottomNavigationView
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
        // Hiển thị BottomNavigationBar nếu đang ở trang HomeFragment
        bottomNavigationView.setVisibility(View.VISIBLE);

        getUsers();
        searchView = binding.searchNewFriend;
        searchView.setIconifiedByDefault(false);

        // Khởi tạo UsersAdapter và đặt adapter cho RecyclerView
        List<User> users = new ArrayList<>();
        UsersAdapter usersAdapter = new UsersAdapter(users, this);
        binding.rcvAddFriend.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rcvAddFriend.setAdapter(usersAdapter);

// Tạo SearchView và lắng nghe sự kiện tìm kiếm
        SearchView searchView = binding.searchNewFriend;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("TAG", "onQueryTextChange: co vao");
                usersAdapter.getFilter().filter(newText);
                return true;
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
    private void getUsers(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        List<User> users = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                            if (currentUserId.equals(documentSnapshot.getId())) {
                                continue;
                            }

                            User user = new User();
                            user.name = documentSnapshot.getString(Constants.KEY_NAME);
                            user.email = documentSnapshot.getString(Constants.KEY_EMAIL);
                            user.image = documentSnapshot.getString(Constants.KEY_IMAGE);
                            user.token = documentSnapshot.getString(Constants.KEY_ALOHA_TOKEN);
                            user.id = documentSnapshot.getId();
                            users.add(user);
                        }
                        // Bước 2: Lấy dữ liệu từ bộ sưu tập "friend" để xác định trạng thái
                        database.collection(Constants.KEY_COLLECTION_FRIEND)
                                .addSnapshotListener((snapshot, error) -> {
                                    if (error != null) {
                                        Log.d("TAG", "Error listening for friend collection changes: ", error);
                                        return;
                                    }

                                    if (snapshot != null && !snapshot.isEmpty()) {
                                        List<Friend> friendList = new ArrayList<>();
                                        for (DocumentSnapshot documentSnapshot : snapshot.getDocuments()) {
                                            Friend friend1 = new Friend();
                                            friend1.senderId = documentSnapshot.getString(Constants.KEY_SENDER_ID);
                                            friend1.receiverId = documentSnapshot.getString(Constants.KEY_RECEIVER_ID);
                                            friend1.friendConnect = documentSnapshot.getLong(Constants.KEY_FRIEND_CONNECT);
                                            friendList.add(friend1);
                                        }

                                        // Cập nhật danh sách bạn bè và trạng thái người dùng

                                        updateFriendList(friendList, users);
                                    }
                                });



                        if (users.size() > 0) {
                            Log.d("TAG_users.size", "getUsers: ");
                            UsersAdapter usersAdapter1 = new UsersAdapter(users, this);
                            binding.rcvAddFriend.setLayoutManager(new LinearLayoutManager(requireContext()));
                            binding.rcvAddFriend.setAdapter(usersAdapter1);

// Tìm kiếm người dùng khi có thay đổi văn bản trong SearchView
                            binding.searchNewFriend.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                @Override
                                public boolean onQueryTextSubmit(String query) {
                                    return false;
                                }

                                @Override
                                public boolean onQueryTextChange(String newText) {
                                    Log.d("TAG_search", "onQueryTextChange: " + newText);
                                    usersAdapter1.getFilter().filter(newText);
                                    return true;
                                }
                            });
                            usersAdapter1.setPopupMenuItemClickListener(new UsersAdapter.OnPopupMenuItemClickListener() {
                                @Override
                                public void onPopupMenuItemClick(User user, int itemId) {
                                    // Xử lý sự kiện click với đối tượng User và itemId
                                    Log.d("TAG_user", "onPopupMenuItemClick: " + user.id);
                                    Log.d("TAG_user_collection", "onPopupMenuItemClick: " + preferenceManager.getString(Constants.KEY_USER_ID));


                                        if (itemId == R.id.menu_item_profile) {
                                            // Gọi phương thức addFriendConnect(userId) để thêm bạn bè

                                        } else if (itemId == R.id.menu_item_add_friend) {
                                            // Gọi phương thức removeFriendConnect(userId) để xóa bạn bè
                                            addFriendConnect(user);
                                        } else if (itemId == R.id.menu_item_send_friend) {

                                        } else if (itemId == R.id.menu_item_success_friend) {
//                                            updateFriend(1);
                                        } else if (itemId == R.id.menu_item_block) {

                                        }
                                }
                            });
                            binding.rcvAddFriend.setVisibility(View.VISIBLE);
                        } else {
                            showErrorMessage();
                        }
                    } else {
                        showErrorMessage();
                    }
                })
                .addOnFailureListener(e -> {
                    loading(false);
                    showErrorMessage();
                });
    }

    private void updateFriendList(List<Friend> friendList,List<User> users) {
        Log.d("TAG_updateFriendList", "updateFriendList: ");
        for (User user : users) {
            String receiverId = user.getId();
            for (Friend friend : friendList) {
                if (Objects.equals(preferenceManager.getString(Constants.KEY_USER_ID), friend.senderId)) {
                    if (friend.friendConnect == 0) {
                        if (Objects.equals(receiverId, friend.receiverId)) {
                            user.setStatus(1);
                        }
                    } else if (friend.friendConnect == 1) {
                        if (Objects.equals(receiverId, friend.receiverId)) {
                            user.setStatus(3);
                        }
                    }
                } else if (Objects.equals(preferenceManager.getString(Constants.KEY_USER_ID), friend.receiverId)) {
                    if (friend.friendConnect == 0) {
                        if (Objects.equals(receiverId, friend.senderId)) {
                            user.setStatus(2);
                        }
                    }
                }
            }
        }

        // Cập nhật lại danh sách người dùng trong adapter
        UsersAdapter usersAdapter = new UsersAdapter(users, this);
        binding.rcvAddFriend.setAdapter(usersAdapter);

        usersAdapter.setPopupMenuItemClickListener(new UsersAdapter.OnPopupMenuItemClickListener() {
            @Override
            public void onPopupMenuItemClick(User user, int itemId) {
                // Xử lý sự kiện click với đối tượng User và itemId
                Log.d("TAG_user", "onPopupMenuItemClick: " + user.id);
                Log.d("TAG_user_collection", "onPopupMenuItemClick: " + preferenceManager.getString(Constants.KEY_USER_ID));


                if (itemId == R.id.menu_item_profile) {
                    // Gọi phương thức addFriendConnect(userId) để thêm bạn bè

                } else if (itemId == R.id.menu_item_add_friend) {
                    // Gọi phương thức removeFriendConnect(userId) để xóa bạn bè
                    addFriendConnect(user);
                } else if (itemId == R.id.menu_item_send_friend) {

                } else if (itemId == R.id.menu_item_success_friend) {
//                    updateFriend(1);
                } else if (itemId == R.id.menu_item_block) {

                }
            }
        });
    }

    private void addFriendConnect(User receiverUser){
        Log.d("TAG_co_vao_4", "addFriendConnect: ");
        HashMap<String, Object> friend = new HashMap<>();
        friend.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        friend.put(Constants.KEY_SENDER_EMAIL, preferenceManager.getString(Constants.KEY_EMAIL));
        friend.put(Constants.KEY_SENDER_NAME, preferenceManager.getString(Constants.KEY_NAME));
        Log.d("TAG_name", "sendMessage: " + preferenceManager.getString(Constants.KEY_NAME));
        friend.put(Constants.KEY_SENDER_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE));
        friend.put(Constants.KEY_RECEIVER_ID,  receiverUser.id);
        friend.put(Constants.KEY_RECEIVER_EMAIL, receiverUser.email);
        friend.put(Constants.KEY_RECEIVER_NAME, receiverUser.name);
        friend.put(Constants.KEY_RECEIVER_IMAGE, receiverUser.image);
        friend.put(Constants.KEY_FRIEND_CONNECT, 0);
        Log.d("TAG_User_new", "sendMessage: "+receiverUser.name);
        friend.put(Constants.KEY_TIMESTAMP, new Date());
        addFriend(friend);
    }

    private void addFriend(HashMap<String, Object> friend) {
        Log.d("TAG_addFriend", "addFriend: ");
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection(Constants.KEY_COLLECTION_FRIEND)
                .add(friend)
                .addOnSuccessListener(documentReference -> {
                    friendId = documentReference.getId();
//                    updateFriendListOnOtherDevice(receiverUser);
                });
    }

    private void updateFriend(int friendConnect){
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_FRIEND).document(friendId);
        documentReference.update(
                Constants.KEY_FRIEND_CONNECT, friendConnect,
                Constants.KEY_TIMESTAMP, new Date()
        );
    }

    private void checkForFriend(String receiverId){
        checkForFriendRemotely(
                preferenceManager.getString(Constants.KEY_USER_ID),
                receiverId
        );
        checkForFriendRemotely(
                receiverId,
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
    }

    private void checkForFriendRemotely(String senderId, String receiverId) {
        database.collection(Constants.KEY_COLLECTION_FRIEND)
                .whereEqualTo(Constants.KEY_SENDER_ID, senderId)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverId)
                .get()
                .addOnCompleteListener(conversionOnCompletionListener);
    }

    private final OnCompleteListener<QuerySnapshot> conversionOnCompletionListener = new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                friendId = documentSnapshot.getId();
            }
        }
    };

    private void updateFriendListOnOtherDevice(User receiverUser) {
        // Gửi thông báo cập nhật danh sách bạn bè cho máy khác
        // Sử dụng Firebase Cloud Messaging (FCM) hoặc cách truyền thông tin khác
        // để thông báo cho máy khác cập nhật danh sách bạn bè.
    }

    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s","No user available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBarAddFriend.setVisibility(View.VISIBLE);
        } else {
            binding.progressBarAddFriend.setVisibility(View.GONE);
        }

    }

    @Override
    public void onUserClicked(User user) {

        Bundle args = new Bundle();
        args.putParcelable(Constants.KEY_USER, (Parcelable) user); // Ví dụ: đính kèm đối tượng User vào Bundle với khóa "user"

        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);


        FragmentManager fragmentManager = getFragmentManager();
        assert fragmentManager != null;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);

        fragmentTransaction.commit();
    }
}