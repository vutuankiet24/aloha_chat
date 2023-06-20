package com.example.aloha.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aloha.databinding.ItemContainerFriendBinding;
import com.example.aloha.databinding.ItemContainerFriendReviewBinding;
import com.example.aloha.listeners.FriendListener;
import com.example.aloha.models.Friend;
import com.example.aloha.models.User;
import com.example.aloha.utilities.Constants;
import com.example.aloha.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FriendReviewAdapter extends RecyclerView.Adapter<FriendReviewAdapter.FriendViewHolder>{
    private final List<Friend> friendList;
    private final FriendListener friendListener;

    public FriendReviewAdapter(List<Friend> friendList, FriendListener friendListener) {
        this.friendList = friendList;
        this.friendListener = friendListener;
    }

    @NonNull
    @Override
    public FriendReviewAdapter.FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo view holder và inflating layout cho một item friend
        ItemContainerFriendReviewBinding itemContainerFriendReviewBinding = ItemContainerFriendReviewBinding.inflate(
                LayoutInflater.from(parent.getContext()),parent,false
        );
        return new FriendReviewAdapter.FriendViewHolder(itemContainerFriendReviewBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendReviewAdapter.FriendViewHolder holder, int position) {
        // Gán dữ liệu từ friendList vào view holder
        Friend friend = friendList.get(position);
        Log.d("TAG_onBindViewHolder", "onBindViewHolder: "+friend);
        holder.setFriendData(friend);
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {

        ItemContainerFriendReviewBinding binding;
        PreferenceManager preferenceManager;
        public FriendViewHolder(ItemContainerFriendReviewBinding itemContainerFriendReviewBinding) {
            super(itemContainerFriendReviewBinding.getRoot());
            binding=itemContainerFriendReviewBinding;
            preferenceManager = new PreferenceManager(itemView.getContext());
        }

        public void setFriendData(Friend friend) {
            Log.d("TAG_userEmail", "bind: "+preferenceManager.getString(Constants.KEY_USER_ID));

            // Hiển thị thông tin bạn bè trong view holder
            if(Objects.equals(preferenceManager.getString(Constants.KEY_USER_ID), friend.senderId)){
                Log.d("TAG_userEmail", "bind: "+friend.receiverEmail);
                binding.userName.setText(friend.receiverName);
                Glide.with(itemView.getContext())
                        .load(friend.receiverImage)
                        .into(binding.imageViewUser);
                binding.getRoot().setOnClickListener(v -> {
                    User user = new User();
                    user.id = friend.receiverId;
                    user.name = friend.receiverName;
                    user.image = friend.receiverImage;
                    user.email = friend.receiverEmail;
                    friendListener.onFriendClicked(user);
                });
            }
            if(Objects.equals(preferenceManager.getString(Constants.KEY_USER_ID), friend.receiverId)){
                Log.d("TAG_userEmail", "bind: "+friend.receiverEmail);
                binding.userName.setText(friend.senderName);
                Glide.with(itemView.getContext())
                        .load(friend.senderImage)
                        .into(binding.imageViewUser);
                binding.getRoot().setOnClickListener(v -> {
                    User user = new User();
                    user.id = friend.senderId;
                    user.name = friend.senderName;
                    user.image = friend.senderImage;
                    user.email = friend.senderEmail;
                    friendListener.onFriendClicked(user);
                });
            }
        }
    }
}
