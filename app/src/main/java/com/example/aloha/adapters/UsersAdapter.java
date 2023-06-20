package com.example.aloha.adapters;

import android.text.TextUtils;
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
import com.example.aloha.databinding.ItemContainerUserBinding;
import com.example.aloha.listeners.UserListener;
import com.example.aloha.models.User;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> implements Filterable {
    private final List<User> users;

    private final UserListener userListener;
    private List<User> filteredUsers;
    private OnPopupMenuItemClickListener popupMenuItemClickListener;


    public UsersAdapter(List<User> users, UserListener userListener) {
        this.users = users;
        this.filteredUsers = new ArrayList<>(users);
        this.userListener = userListener;
    }

    // Phương thức để đặt giá trị cho popupMenuItemClickListener
    public void setPopupMenuItemClickListener(OnPopupMenuItemClickListener listener) {
        this.popupMenuItemClickListener = listener;
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<User> filteredList = new ArrayList<>();
                Log.d("TAG_filteredList", "performFiltering: "+ filteredList.size());
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(filteredUsers); // Thay đổi từ users thành filteredUsers
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (User user : filteredUsers) { // Thay đổi từ users thành filteredUsers
                        if (user.getName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(user);
                            Log.d("TAG_filteredUsers", "performFiltering: "+ filteredList.size());
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                Log.d("TAG_results", "performFiltering: "+ results.values);

                return results;

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                users.clear(); // Thay đổi từ filteredUsers thành users
                users.addAll((List<User>) results.values);
                Log.d("TAG", "publishResults: " + results.values);
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),parent,false
        );
        return new UserViewHolder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.setUserData(user);

        holder.binding.popupAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị Popup Menu
                showPopupMenu(v, user);
            }
        });
    }


    private void showPopupMenu(View view, User user) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_menu_add_friend, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (popupMenuItemClickListener != null) {
                    // Truyền cả đối tượng User, itemId và menu vào phương thức onPopupMenuItemClick()
                    popupMenuItemClickListener.onPopupMenuItemClick(user, item.getItemId());
                }
                return true;
            }
        });

        // Tùy chỉnh visibility cho item trong PopupMenu
        if(user.status == 1){
            Menu menu = popupMenu.getMenu();
            MenuItem sendFriendItem = menu.findItem(R.id.menu_item_send_friend);
            sendFriendItem.setVisible(true); // Đặt visibility của item thành false
            MenuItem addFriendItem = menu.findItem(R.id.menu_item_add_friend);
            addFriendItem.setVisible(false);
        } else if(user.status == 2){
            Menu menu = popupMenu.getMenu();
            MenuItem successFriendItem = menu.findItem(R.id.menu_item_success_friend);
            successFriendItem.setVisible(true); // Đặt visibility của item thành false
            MenuItem addFriendItem = menu.findItem(R.id.menu_item_add_friend);
            addFriendItem.setVisible(false);
        } else if(user.status == 3){
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
        void onPopupMenuItemClick(User user,int itemId);
    }


    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        ItemContainerUserBinding binding;

        UserViewHolder(ItemContainerUserBinding itemContainerUserBinding){
            super(itemContainerUserBinding.getRoot());

            binding = itemContainerUserBinding;
        }

        void setUserData(User user){
            binding.userName.setText(user.name);
            binding.userEmail.setText(user.email);
            Glide.with(itemView.getContext())
                    .load(user.image)
                    .into(binding.imageViewUser);
            binding.getRoot().setOnClickListener(v -> userListener.onUserClicked(user));

        }
    }

}
