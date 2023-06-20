package com.example.aloha.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aloha.R;

import java.util.ArrayList;
import java.util.List;

public class FriendReviewChatsAdapter extends RecyclerView.Adapter<FriendReviewChatsAdapter.ViewHolder> {

    private List<String> dataList;

    public FriendReviewChatsAdapter() {
        // Khởi tạo danh sách dữ liệu
        dataList = new ArrayList<>();
        dataList.add("Item 1");
        dataList.add("Item 2");
        dataList.add("Item 3");
        dataList.add("Item 1");
        dataList.add("Item 2");
        dataList.add("Item 3");
        dataList.add("Item 1");
        dataList.add("Item 2");
        dataList.add("Item 3");
        dataList.add("Item 1");
        dataList.add("Item 2");
        dataList.add("Item 3");
        dataList.add("Item 1");
        dataList.add("Item 2");
        dataList.add("Item 3");
        dataList.add("Item 1");
        dataList.add("Item 2");
        dataList.add("Item 3");
        // Thêm dữ liệu khác nếu cần

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo view và view holder cho mỗi mục trong RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_friend_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Đặt dữ liệu vào các thành phần của view holder
        String item = dataList.get(position);
        holder.textView.setText(item);
    }

    @Override
    public int getItemCount() {
        // Trả về số lượng mục trong danh sách dữ liệu
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các thành phần trong view holder
            textView = itemView.findViewById(R.id.user_name);
        }
    }
}

