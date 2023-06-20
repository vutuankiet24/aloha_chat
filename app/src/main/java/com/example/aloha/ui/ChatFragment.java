package com.example.aloha.ui;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.aloha.R;
import com.example.aloha.adapters.ChatAdapter;
import com.example.aloha.adapters.ChatsAdapter;
import com.example.aloha.databinding.FragmentAddFriendBinding;
import com.example.aloha.databinding.FragmentMessageBinding;
import com.example.aloha.listeners.OnBackPressedListener;
import com.example.aloha.models.ChatMessage;
import com.example.aloha.models.User;
import com.example.aloha.utilities.Constants;
import com.example.aloha.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.remote.WatchChange;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatFragment extends Fragment {

    private FragmentMessageBinding binding;
    private BottomNavigationView bottomNavigationView;
    private User receiverUser;
    private OnBackPressedListener onBackPressedListener;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private String conversionId = null;
    private boolean isReceiverAvailable = false;
    private Activity activity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
    }
    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_message, container, false);

        binding = FragmentMessageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        // Khởi tạo BottomNavigationView
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);

        // Hiển thị BottomNavigationBar nếu đang ở trang HomeFragment
        bottomNavigationView.setVisibility(View.GONE);

        setListeners();
        loadReceiverDetails();
        init();
        listenMessages();

        return view;
    }

    private void init(){
        preferenceManager = new PreferenceManager(getContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chatMessages,
                preferenceManager.getString(Constants.KEY_USER_ID),
                receiverUser.image
        );
        binding.rcvChat.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private void sendMessage() {
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID, receiverUser.id);
        message.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, new Date());
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        if(conversionId != null){
            updateConversion(binding.inputMessage.getText().toString(),preferenceManager.getString(Constants.KEY_USER_ID),preferenceManager.getString(Constants.KEY_IMAGE));
            Log.d("TAG_User_new", "sendMessage: "+preferenceManager.getString(Constants.KEY_USER_ID));
        } else {
            HashMap<String, Object> conversion = new HashMap<>();
            conversion.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
            conversion.put(Constants.KEY_SENDER_NAME, preferenceManager.getString(Constants.KEY_NAME));
            Log.d("TAG_name", "sendMessage: " + preferenceManager.getString(Constants.KEY_NAME));
            conversion.put(Constants.KEY_SENDER_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE));
            conversion.put(Constants.KEY_RECEIVER_ID,  receiverUser.id);
            conversion.put(Constants.KEY_RECEIVER_NAME, receiverUser.name);
            conversion.put(Constants.KEY_RECEIVER_IMAGE, receiverUser.image);
            conversion.put(Constants.KEY_LAST_MESSAGE, binding.inputMessage.getText().toString());
            Log.d("TAG_User_new", "sendMessage: "+preferenceManager.getString(Constants.KEY_USER_ID));
            conversion.put(Constants.KEY_LAST_MESSAGE_USER, preferenceManager.getString(Constants.KEY_USER_ID));
            conversion.put(Constants.KEY_LAST_IMAGE_USER, preferenceManager.getString(Constants.KEY_IMAGE));
            conversion.put(Constants.KEY_TIMESTAMP, new Date());
            addConversion(conversion);
        }
        binding.inputMessage.setText(null);

    }

    private void listenAvailabilityOfReceiver(){
        database.collection(Constants.KEY_COLLECTION_USERS).document(receiverUser.id)
                .addSnapshotListener(Executors.newSingleThreadExecutor(), (value, error) -> {
                    if(error != null){
                        return;
                    }
                    if(value != null){
                        Long availability = value.getLong(Constants.KEY_AVAILABILITY);
                        if(availability != null){
                            int availabilityInt = availability.intValue();
                            isReceiverAvailable = availabilityInt == 1;
                        }
                        receiverUser.token = value.getString(Constants.KEY_ALOHA_TOKEN);
                    }
                    activity.runOnUiThread(() -> {
                        if(isReceiverAvailable) {
                            binding.dotAvailability.setVisibility(View.VISIBLE);
                        } else {
                            binding.dotAvailability.setVisibility(View.GONE);
                        }
                    });
                });
    }






    private void listenMessages(){
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUser.id)
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, receiverUser.id)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
      if(error != null){
          return;
      }
      if(value != null){
          int count = chatMessages.size();
          for (DocumentChange documentChange : value.getDocumentChanges()) {
              if(documentChange.getType() == DocumentChange.Type.ADDED){
                  ChatMessage chatMessage = new ChatMessage();
                  chatMessage.senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                  chatMessage.receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                  chatMessage.message = documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                  chatMessage.dateTime = getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                  chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                  chatMessages.add(chatMessage);
              }

          }
          Collections.sort(chatMessages, (obj1,obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
          if(count == 0){
              chatAdapter.notifyDataSetChanged();
          } else {
              chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
              binding.rcvChat.smoothScrollToPosition(chatMessages.size() - 1);
          }
          binding.rcvChat.setVisibility(View.VISIBLE);
      }
      binding.progressBar.setVisibility(View.GONE);
      if(conversionId == null){
          checkForConversion();
      }
    };

    private void loadReceiverDetails() {

        Bundle args = getArguments();
        if (args != null) {
            receiverUser = args.getParcelable(Constants.KEY_USER);
            // Sử dụng dữ liệu User trong Fragment B

            binding.textName.setText(receiverUser.name);
            Glide.with(getContext())
                    .load(receiverUser.image)
                    .into(binding.imageUser);
        }
    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(v -> {
            // Tạo một đối tượng SignUpFragment mới
            ChatsFragment chatsFragment = new ChatsFragment();

            // Thực hiện chuyển đổi Fragment
            FragmentManager fragmentManager = getFragmentManager();
            assert fragmentManager != null;
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, chatsFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

            // Khởi tạo BottomNavigationView
            bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
            // Hiển thị BottomNavigationBar nếu đang ở trang HomeFragment
            bottomNavigationView.setVisibility(View.VISIBLE);
            // Thiết lập biểu tượng hoạt động (active icon) cho mục đang chọn
            bottomNavigationView.getMenu().findItem(R.id.navigation_chats).setChecked(true);
        });

        binding.layoutSend.setOnClickListener(v -> sendMessage());
    }

    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd,yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    private void addConversion(HashMap<String, Object> conversion){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .add(conversion)
                .addOnSuccessListener(documentReference -> conversionId = documentReference.getId());
    }

    private void updateConversion(String message, String userId, String imageUser){
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversionId);
        documentReference.update(
                Constants.KEY_LAST_MESSAGE, message,
                Constants.KEY_LAST_MESSAGE_USER, userId,
                Constants.KEY_LAST_IMAGE_USER, imageUser,
                Constants.KEY_TIMESTAMP, new Date()
        );
    }

    private void checkForConversion(){
        if(chatMessages.size() != 0){
            checkForConversionRemotely(
                    preferenceManager.getString(Constants.KEY_USER_ID),
                    receiverUser.id
            );
            checkForConversionRemotely(
                    receiverUser.id,
                    preferenceManager.getString(Constants.KEY_USER_ID)
            );
        }
    }

    private void checkForConversionRemotely(String senderId, String receiverId) {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
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
                conversionId = documentSnapshot.getId();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (isAdded()) {
            listenAvailabilityOfReceiver();
        }
    }
}