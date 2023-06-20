package com.example.aloha.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

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

import com.example.aloha.R;
import com.example.aloha.adapters.FriendAdapter;
import com.example.aloha.databinding.FragmentListFriendBinding;
import com.example.aloha.listeners.FriendListener;
import com.example.aloha.models.Friend;
import com.example.aloha.models.User;
import com.example.aloha.utilities.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListFriendFragment extends Fragment implements FriendListener {
    private BottomNavigationView bottomNavigationView;
    Toolbar mToolbar;
    SearchView searchView;
    private FirebaseFirestore database;
    private FragmentListFriendBinding binding;
    private com.example.aloha.utilities.PreferenceManager preferenceManager;
    public ListFriendFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_list_friend, container, false);

        binding = FragmentListFriendBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        preferenceManager = new com.example.aloha.utilities.PreferenceManager(getContext());

        mToolbar = view.findViewById(R.id.list_friend_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setTitle("List Friend");

        // Khởi tạo BottomNavigationView
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
        // Hiển thị BottomNavigationBar nếu đang ở trang HomeFragment
        bottomNavigationView.setVisibility(View.VISIBLE);

        database = FirebaseFirestore.getInstance();

        getFriend();
        searchView = binding.searchListFriend;
        searchView.setIconifiedByDefault(false);

        List<Friend> friends = new ArrayList<>();
        FriendAdapter friendAdapter1 = new FriendAdapter(friends, this);
        binding.rcvListFriend.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rcvListFriend.setAdapter(friendAdapter1);

        searchView = binding.searchListFriend;
        searchView.setIconifiedByDefault(false);

        SearchView searchView = binding.searchListFriend;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                friendAdapter1.getFilter().filter(newText);
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

    public void getFriend(){
        // Lấy danh sách bạn bè có friendConnect = 0 từ Firestore
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_FRIEND)
                .whereEqualTo(Constants.KEY_FRIEND_CONNECT, 1)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        Log.d("TAG", "Error listening for friend collection changes: ", error);
                        return;
                    }

                    if (snapshot != null && snapshot.isEmpty()) {
                        Log.d("TAG_empty", "getFriend: ");
                        List<Friend> friendList = new ArrayList<>();
                        updateFriendList(friendList);

                    }

                    if (snapshot != null && !snapshot.isEmpty()) {
                        List<Friend> friendList = new ArrayList<>();
                        Log.d("TAG_userId", "getUserId: "+preferenceManager.getString(Constants.KEY_USER_ID));
                        String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                        for (DocumentSnapshot documentSnapshot : snapshot.getDocuments()) {
                            Friend friend = new Friend();
                            friend.senderId = documentSnapshot.getString(Constants.KEY_SENDER_ID);
                            Log.d("TAG_senderId", "getFriend: "+friend.senderId);
                            friend.senderEmail = documentSnapshot.getString(Constants.KEY_SENDER_EMAIL);
                            friend.senderName = documentSnapshot.getString(Constants.KEY_SENDER_NAME);
                            friend.senderImage = documentSnapshot.getString(Constants.KEY_SENDER_IMAGE);
                            friend.receiverId = documentSnapshot.getString(Constants.KEY_RECEIVER_ID);
                            friend.receiverEmail = documentSnapshot.getString(Constants.KEY_RECEIVER_EMAIL);
                            friend.receiverName = documentSnapshot.getString(Constants.KEY_RECEIVER_NAME);
                            friend.receiverImage = documentSnapshot.getString(Constants.KEY_RECEIVER_IMAGE);
                            friend.friendConnect = documentSnapshot.getLong(Constants.KEY_FRIEND_CONNECT);
                            friend.id = documentSnapshot.getId();
//                            friendList.add(friend);

                            if (friend.senderId.equals(currentUserId) || friend.receiverId.equals(currentUserId)) {
                                // Thêm bạn bè vào danh sách chỉ khi người đăng nhập là người gửi hoặc người nhận
                                friendList.add(friend);
                            }
                        }
                        // Cập nhật danh sách bạn bè và trạng thái người dùng

                        updateFriendList(friendList);
                        if (!friendList.isEmpty()) {
                            FriendAdapter friendAdapter = new FriendAdapter(friendList, this);
                            binding.rcvListFriend.setLayoutManager(new LinearLayoutManager(requireContext()));
                            binding.rcvListFriend.setAdapter(friendAdapter);

                            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                @Override
                                public boolean onQueryTextSubmit(String query) {
                                    return false;
                                }

                                @Override
                                public boolean onQueryTextChange(String newText) {
                                    friendAdapter.getFilter().filter(newText);
                                    return true;
                                }
                            });
                            friendAdapter.setPopupMenuItemClickListener(new FriendAdapter.OnPopupMenuItemClickListener() {
                                @Override
                                public void onPopupMenuItemClick(Friend friend, int itemId) {
                                    // Xử lý sự kiện click với đối tượng User và itemId

                                    if (itemId == R.id.menu_item_profile) {
                                        // Gọi phương thức addFriendConnect(userId) để thêm bạn bè

                                    } else if (itemId == R.id.menu_item_add_friend) {
                                        // Gọi phương thức removeFriendConnect(userId) để xóa bạn bè

                                    } else if (itemId == R.id.menu_item_send_friend) {

                                    } else if (itemId == R.id.menu_item_success_friend) {
//                                            updateFriend(1);
//                                        updateFriend(1, friend.id);
                                    } else if (itemId == R.id.menu_item_block) {

                                    } else if (itemId == R.id.menu_item_delete_friend) {
                                        deleteFriend(friend.id);
                                    }
                                }
                            });

                            binding.rcvListFriend.setVisibility(View.VISIBLE);
                            binding.progressBarListFriend.setVisibility(View.GONE);
                        }else {
                            Log.d("TAG_empty", "getFriend: ");
                            FriendAdapter friendAdapter = new FriendAdapter(friendList, this);
                            binding.rcvListFriend.setLayoutManager(new LinearLayoutManager(requireContext()));
                            binding.rcvListFriend.setAdapter(friendAdapter);
                            binding.rcvListFriend.setVisibility(View.GONE);
                            binding.progressBarListFriend.setVisibility(View.VISIBLE);
                        }
                    }

                });
    }

    private void updateFriendList(List<Friend> friendList) {
        Log.d("TAG_updateFriendList", "updateFriendList: co vao");
        if(friendList.isEmpty()){
            Log.d("TAG_empty", "getFriend: ");
            FriendAdapter friendAdapter = new FriendAdapter(friendList, this);
            binding.rcvListFriend.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.rcvListFriend.setAdapter(friendAdapter);
            binding.rcvListFriend.setVisibility(View.GONE);
            binding.progressBarListFriend.setVisibility(View.VISIBLE);
        }
        for (Friend friend : friendList) {
                if (Objects.equals(preferenceManager.getString(Constants.KEY_USER_ID), friend.senderId)) {
                    if (friend.friendConnect == 1) {
                        friend.setStatus(3);
                    }
                } else if (Objects.equals(preferenceManager.getString(Constants.KEY_USER_ID), friend.receiverId)) {
                    if (friend.friendConnect == 1) {
                        friend.setStatus(3);
                    }
                }

        }

        // Cập nhật lại danh sách người dùng trong adapter
        FriendAdapter friendAdapter = new FriendAdapter(friendList, this);
        binding.rcvListFriend.setAdapter(friendAdapter);

        friendAdapter.setPopupMenuItemClickListener(new FriendAdapter.OnPopupMenuItemClickListener() {
            @Override
            public void onPopupMenuItemClick(Friend friend, int itemId) {
                // Xử lý sự kiện click với đối tượng User và itemId

                if (itemId == R.id.menu_item_profile) {
                    // Gọi phương thức addFriendConnect(userId) để thêm bạn bè

                } else if (itemId == R.id.menu_item_add_friend) {
                    // Gọi phương thức removeFriendConnect(userId) để xóa bạn bè

                } else if (itemId == R.id.menu_item_send_friend) {

                } else if (itemId == R.id.menu_item_success_friend) {
//                    updateFriend(1, friend.id);
                } else if (itemId == R.id.menu_item_block) {

                } else if (itemId == R.id.menu_item_delete_friend) {
                    deleteFriend(friend.id);
                }
            }
        });
    }

    public void deleteFriend(String friendId) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_FRIEND)
                .document(friendId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Xóa thành công
                    Log.d("TAG", "Friend deleted successfully 1");
                    getFriend();

                })
                .addOnFailureListener(e -> {
                    // Xảy ra lỗi trong quá trình xóa
                    Log.d("TAG", "Error deleting friend", e);
                });
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

    @Override
    public void onFriendClicked(User user) {
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