package com.example.aloha.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
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
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.aloha.adapters.ChatsAdapter;
import com.example.aloha.adapters.FriendReviewAdapter;
import com.example.aloha.adapters.FriendReviewChatsAdapter;
import com.example.aloha.adapters.RecentConversationsAdapter;
import com.example.aloha.databinding.FragmentChatsBinding;
import com.example.aloha.listeners.ConversionListener;
import com.example.aloha.listeners.FriendListener;
import com.example.aloha.models.ChatMessage;
import com.example.aloha.models.Friend;
import com.example.aloha.models.User;
import com.example.aloha.utilities.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ChatsFragment extends Fragment implements ConversionListener, FriendListener {
    private BottomNavigationView bottomNavigationView;
    Toolbar mToolbar;
    private boolean isDarkMode = false;
    private List<ChatMessage> conversations;
    private RecentConversationsAdapter conversationsAdapter;
    private FirebaseFirestore database;
    private FragmentChatsBinding binding;
    SearchView searchView;
    private String searchQuery = "";
    private com.example.aloha.utilities.PreferenceManager preferenceManager;
    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_home, container, false);
//        setHasOptionsMenu(true);
//        return view;

        binding = FragmentChatsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        preferenceManager = new com.example.aloha.utilities.PreferenceManager(getContext());

        mToolbar = view.findViewById(R.id.chats_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setTitle("Chats");

        // Khởi tạo BottomNavigationView
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
        // Hiển thị BottomNavigationBar nếu đang ở trang HomeFragment
        bottomNavigationView.setVisibility(View.VISIBLE);

        getToken();
        init();
        listenConversations();
        getFriend();

        List<Friend> friends = new ArrayList<>();
        FriendReviewAdapter friendReviewAdapter = new FriendReviewAdapter(friends, this);
        binding.rcvFriends.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rcvFriends.setAdapter(friendReviewAdapter);

//        conversations = new ArrayList<>();
//        RecentConversationsAdapter recentConversationsAdapter = new RecentConversationsAdapter(conversations, this);
//        binding.rcvConversations.setLayoutManager(new LinearLayoutManager(getContext()));
//        binding.rcvConversations.setAdapter(recentConversationsAdapter);
//
//
//        searchView = binding.searchChats;
//        searchView.setIconifiedByDefault(false);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                recentConversationsAdapter.getFilter().filter(newText);
//                return true;
//            }
//        });
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
                            FriendReviewAdapter friendReviewAdapter = new FriendReviewAdapter(friendList, this);
                            binding.rcvFriends.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                            binding.rcvFriends.setAdapter(friendReviewAdapter);
                            binding.rcvFriends.setVisibility(View.VISIBLE);
                        }else {
                            Log.d("TAG_empty", "getFriend: ");
                            FriendReviewAdapter friendReviewAdapter = new FriendReviewAdapter(friendList, this);
                            binding.rcvFriends.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                            binding.rcvFriends.setAdapter(friendReviewAdapter);
                            binding.rcvFriends.setVisibility(View.GONE);
                        }
                    }

                });
    }

    private void updateFriendList(List<Friend> friendList) {
        Log.d("TAG_updateFriendList", "updateFriendList: co vao");
        if(friendList.isEmpty()){
            Log.d("TAG_empty", "getFriend: ");
            FriendReviewAdapter friendReviewAdapter = new FriendReviewAdapter(friendList, this);
            binding.rcvFriends.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
            binding.rcvFriends.setAdapter(friendReviewAdapter);
            binding.rcvFriends.setVisibility(View.GONE);
        }

        // Cập nhật lại danh sách người dùng trong adapter
        FriendReviewAdapter friendReviewAdapter = new FriendReviewAdapter(friendList, this);
        binding.rcvFriends.setAdapter(friendReviewAdapter);
    }

    private void listenConversations(){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        Log.d("TAG", "co vao");
        if(error != null) {
            return;
        }
        if(value != null) {
            Log.d("TAG", "co vao1");
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if(documentChange.getType() == DocumentChange.Type.ADDED) {
                    String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = senderId;
                    chatMessage.receiverId = receiverId;
                    if(preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)){
                        chatMessage.conversionImage = documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE);
                        chatMessage.conversionName = documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME);
                        chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    } else {
                        chatMessage.conversionImage = documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE);
                        chatMessage.conversionName = documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                        chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    }
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                    chatMessage.lastMessageUser = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE_USER);
                    chatMessage.lastImageUser = documentChange.getDocument().getString(Constants.KEY_LAST_IMAGE_USER);
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    conversations.add(chatMessage);

                    if(conversations.size()>0){
                        RecentConversationsAdapter recentConversationsAdapter = new RecentConversationsAdapter(conversations, this);
                        binding.rcvConversations.setLayoutManager(new LinearLayoutManager(getContext()));
                        binding.rcvConversations.setAdapter(recentConversationsAdapter);


// Tìm kiếm người dùng khi có thay đổi văn bản trong SearchView
                        binding.searchChats.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                recentConversationsAdapter.getFilter().filter(newText);
                                return true;
                            }
                        });

                    }
                    Log.d("TAG_conversations", ": "+conversations.toString());
                } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    Log.d("TAG", "co vao2");

                    for (int i = 0;i < conversations.size(); i++){
                        String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        if(conversations.get(i).senderId.equals(senderId) && conversations.get(i).receiverId.equals(receiverId)){
                            Log.d("TAG", "co vao3");
                            conversations.get(i).message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                            conversations.get(i).lastMessageUser = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE_USER);
                            conversations.get(i).lastImageUser = documentChange.getDocument().getString(Constants.KEY_LAST_IMAGE_USER);
                            conversations.get(i).dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                            break;
                        }
                    }
                }
            }
            Log.d("TAG", "co vao 4");
            conversationsAdapter = new RecentConversationsAdapter(conversations, this);
            Collections.sort(conversations, (obj1, obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
            binding.rcvConversations.smoothScrollToPosition(0);
            binding.rcvConversations.setAdapter(conversationsAdapter);
            binding.rcvConversations.setVisibility(View.VISIBLE);

            binding.progressBar.setVisibility(View.GONE);
        }
    };

    private void init() {
        conversations = new ArrayList<>();
        Log.d("TAG_conversations", "TAG_conversations: "+conversations.toString());
        conversationsAdapter = new RecentConversationsAdapter(conversations, this);
        binding.rcvConversations.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rcvConversations.setAdapter(conversationsAdapter);
        database = FirebaseFirestore.getInstance();
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

    private void showToast(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void getToken(){
//        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> updateToken(token));
    }

    private void updateToken(String token){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.KEY_ALOHA_TOKEN, token)
//                .addOnSuccessListener(unused -> showToast("Token update successfully"))
                .addOnFailureListener(e -> showToast("Unable to update token"));
    }

    @Override
    public void onConversionClicked(User user) {
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