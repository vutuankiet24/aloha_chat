package com.example.aloha.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aloha.R;
import com.example.aloha.databinding.ItemContainerFriendBinding;
import com.example.aloha.listeners.FriendListener;
import com.example.aloha.models.Friend;
import com.example.aloha.models.User;
import com.example.aloha.utilities.Constants;
import com.example.aloha.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> implements Filterable {
    private final List<Friend> friendList;
    private final FriendListener friendListener;
    private List<Friend> filteredFriend;
    private FriendAdapter.OnPopupMenuItemClickListener popupMenuItemClickListener;

    public FriendAdapter(List<Friend> friendList, FriendListener friendListener) {
        this.friendList = friendList;
        this.filteredFriend = new ArrayList<>(friendList);
        this.friendListener = friendListener;
    }

    // Phương thức để đặt giá trị cho popupMenuItemClickListener
    public void setPopupMenuItemClickListener(FriendAdapter.OnPopupMenuItemClickListener listener) {
        this.popupMenuItemClickListener = listener;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Friend> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(filteredFriend); // Thay đổi từ users thành filteredUsers
                    Log.d("TAG_filteredFriendInvite", "performFiltering: "+ filteredFriend.size());
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Friend friend : filteredFriend) { // Thay đổi từ users thành filteredUsers
                        if (friend.getReceiverName().toLowerCase().contains(filterPattern) || friend.getSenderName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(friend);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                friendList.clear(); // Thay đổi từ filteredUsers thành users
                friendList.addAll((List<Friend>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public FriendAdapter.FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo view holder và inflating layout cho một item friend
        ItemContainerFriendBinding itemContainerFriendBinding = ItemContainerFriendBinding.inflate(
                LayoutInflater.from(parent.getContext()),parent,false
        );
        return new FriendAdapter.FriendViewHolder(itemContainerFriendBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendAdapter.FriendViewHolder holder, int position) {
        // Gán dữ liệu từ friendList vào view holder
        Friend friend = friendList.get(position);
        Log.d("TAG_onBindViewHolder", "onBindViewHolder: "+friend);
        holder.setFriendData(friend);

        holder.binding.popupFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị Popup Menu
                showPopupMenu(v, friend);
            }
        });
    }

    private void showPopupMenu(View view, Friend friend) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_menu_add_friend, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (popupMenuItemClickListener != null) {
                    // Truyền cả đối tượng User, itemId và menu vào phương thức onPopupMenuItemClick()
                    popupMenuItemClickListener.onPopupMenuItemClick(friend, item.getItemId());
                }
                return true;
            }
        });

        // Tùy chỉnh visibility cho item trong PopupMenu
        if(friend.status == 1){
            Menu menu = popupMenu.getMenu();
            MenuItem sendFriendItem = menu.findItem(R.id.menu_item_send_friend);
            sendFriendItem.setVisible(true); // Đặt visibility của item thành false
            MenuItem addFriendItem = menu.findItem(R.id.menu_item_add_friend);
            addFriendItem.setVisible(false);
            MenuItem deleteFriendItem = menu.findItem(R.id.menu_item_delete_friend);
            deleteFriendItem.setVisible(true);
        } else if(friend.status == 2){
            Menu menu = popupMenu.getMenu();
            MenuItem successFriendItem = menu.findItem(R.id.menu_item_success_friend);
            successFriendItem.setVisible(true); // Đặt visibility của item thành false
            MenuItem addFriendItem = menu.findItem(R.id.menu_item_add_friend);
            addFriendItem.setVisible(false);
            MenuItem deleteFriendItem = menu.findItem(R.id.menu_item_delete_friend);
            deleteFriendItem.setVisible(true);
        } else if(friend.status == 3){
            Menu menu = popupMenu.getMenu();
            MenuItem deleteFriendItem = menu.findItem(R.id.menu_item_delete_friend);
            deleteFriendItem.setVisible(true); // Đặt visibility của item thành false
            MenuItem addFriendItem = menu.findItem(R.id.menu_item_add_friend);
            addFriendItem.setVisible(false);
        }

        popupMenu.show();

    }

    // Giao diện (interface) để xử lý sự kiện từ fragment
    public interface OnPopupMenuItemClickListener {
        void onPopupMenuItemClick(Friend friend,int itemId);
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {

        ItemContainerFriendBinding binding;
        PreferenceManager preferenceManager;
        public FriendViewHolder(ItemContainerFriendBinding itemContainerFriendBinding) {
            super(itemContainerFriendBinding.getRoot());
            binding=itemContainerFriendBinding;
            preferenceManager = new PreferenceManager(itemView.getContext());
        }

        public void setFriendData(Friend friend) {
            Log.d("TAG_userEmail", "bind: "+preferenceManager.getString(Constants.KEY_USER_ID));

            // Hiển thị thông tin bạn bè trong view holder
            if(Objects.equals(preferenceManager.getString(Constants.KEY_USER_ID), friend.senderId)){
                Log.d("TAG_userEmail", "bind: "+friend.receiverEmail);
                binding.userEmail.setText(friend.receiverEmail);
                binding.userName.setText(friend.receiverName);
                Glide.with(itemView.getContext())
                        .load(friend.receiverImage)
                        .into(binding.imageViewFriend);
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
                binding.userEmail.setText(friend.senderEmail);
                binding.userName.setText(friend.senderName);
                Glide.with(itemView.getContext())
                        .load(friend.senderImage)
                        .into(binding.imageViewFriend);
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
