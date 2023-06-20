package com.example.aloha.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aloha.R;

import java.util.ArrayList;
import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> implements Filterable {
    private List<String> dataList;
    private List<String> filteredChatList;

    public ChatsAdapter(List<String> chatList) {
        this.dataList = chatList;
        this.filteredChatList = new ArrayList<>(chatList); // Khởi tạo filteredChatList từ chatList
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo view và view holder cho mỗi mục trong RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_items, parent, false);

        // Thiết lập khoảng cách giữa các item (vd: 8dp)
//        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
//        layoutParams.setMargins(8, 0, 8, 0);
//        view.setLayoutParams(layoutParams);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Thiết lập kích thước của itemView (vd: 120dp x 160dp)
//        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
//        layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, holder.itemView.getResources().getDisplayMetrics());
//        layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, holder.itemView.getResources().getDisplayMetrics());
//        holder.itemView.setLayoutParams(layoutParams);

        // Đặt dữ liệu vào các thành phần của view holder
        String item = filteredChatList.get(position);
        holder.textView.setText(item);
    }

    @Override
    public int getItemCount() {
        return filteredChatList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<String> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    // Nếu không có văn bản tìm kiếm, hiển thị toàn bộ danh sách
                    filteredList.addAll(dataList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    // Lọc danh sách theo văn bản tìm kiếm
                    for (String chat : dataList) {
                        if (chat.toLowerCase().contains(filterPattern)) {
                            filteredList.add(chat);
                        }
                    }
                }

                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredChatList = (List<String>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các thành phần trong view holder
            textView = itemView.findViewById(R.id.chat_text);
        }
    }
}
