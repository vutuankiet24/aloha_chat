package com.example.aloha.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aloha.R;
import com.example.aloha.databinding.ItemContainerRecentConversionBinding;
import com.example.aloha.listeners.ConversionListener;
import com.example.aloha.models.ChatMessage;
import com.example.aloha.models.User;
import com.example.aloha.utilities.Constants;
import com.example.aloha.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecentConversationsAdapter extends RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder> implements Filterable {

    private final List<ChatMessage> chatMessages;
    private final ConversionListener conversionListener;

    private List<ChatMessage> filteredChatMessages;

    public RecentConversationsAdapter(List<ChatMessage> chatMessages, ConversionListener conversionListener) {
        this.chatMessages = chatMessages;
        this.filteredChatMessages = new ArrayList<>(chatMessages);
        this.conversionListener = conversionListener;
    }

    @Override
    public Filter getFilter() {
        Log.d("TAG", "getFilter: ");
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<ChatMessage> filteredList = new ArrayList<>();
                Log.d("TAG_mesage", "getFilter: "+ filteredList.size());
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(filteredChatMessages); // Thay đổi từ users thành filteredUsers
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    Log.d("TAG_filterPattern", "performFiltering: "+filterPattern);
                    for (ChatMessage chatMessage : filteredChatMessages) { // Thay đổi từ users thành filteredUsers
                        if (chatMessage.getConversionName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(chatMessage);
                            Log.d("TAG_chatMessage", "performFiltering: "+chatMessage.conversionName);
                            Log.d("TAG_filteredList", "performFiltering: "+ filteredList.size());
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                Log.d("TAG_results", "performFiltering: "+results.values);

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                chatMessages.clear(); // Thay đổi từ filteredUsers thành users
                chatMessages.addAll((List<ChatMessage>) results.values);
                Log.d("TAG_results", "performFiltering: "+chatMessages);
                notifyDataSetChanged();
                Log.d("TAG", "publishResults: co vao");
            }
        };
    }

    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHolder(
                ItemContainerRecentConversionBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        Log.d("TAG", "onBindViewHolder: "+ chatMessage);
        holder.setData(chatMessage);
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder {
        ItemContainerRecentConversionBinding binding;
        PreferenceManager preferenceManager;
        public ConversionViewHolder(ItemContainerRecentConversionBinding itemContainerRecentConversionBinding){
            super(itemContainerRecentConversionBinding.getRoot());
            binding = itemContainerRecentConversionBinding;
            preferenceManager = new PreferenceManager(itemView.getContext());
        }

        public void setData(ChatMessage chatMessage){
            Log.d("TAG_userId", "ConversionViewHolder: "+preferenceManager.getString(Constants.KEY_USER_ID));
            Log.d("TAG_userId", "ConversionViewHolder: "+chatMessage.lastMessageUser);
            Log.d("TAG_name", "setData: "+chatMessage.conversionName);
            Log.d("TAG_message", "setData: "+chatMessage.message);
            Log.d("TAG_image", "setData: "+chatMessage.conversionImage);
            Glide.with(itemView.getContext())
                    .load(chatMessage.conversionImage)
                    .into(binding.imageUser);
            if(Objects.equals(chatMessage.lastMessageUser, preferenceManager.getString(Constants.KEY_USER_ID))){
                Glide.with(itemView.getContext())
                        .load(R.drawable.ic_check_circle)
                        .into(binding.imageCheckSend);
            } else {
                Glide.with(itemView.getContext())
                        .load(preferenceManager.getString(Constants.KEY_IMAGE))
                        .into(binding.imageCheckSend);
            }

            binding.textRecentMessage.setText(chatMessage.message);
            binding.textUserName.setText(chatMessage.conversionName);
            binding.getRoot().setOnClickListener(v -> {
                User user = new User();
                user.id = chatMessage.conversionId;
                user.name = chatMessage.conversionName;
                user.image = chatMessage.conversionImage;
                conversionListener.onConversionClicked(user);
            });

        }
    }
}
