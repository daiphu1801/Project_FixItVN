package com.fixit.feature.customer.chat.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fixit.databinding.ItemChatCustomerBinding;
import com.fixit.feature.customer.chat.domain.model.ChatPreview;

import java.util.List;

/**
 * Adapter hiển thị danh sách preview các hội thoại trên màn hình tin nhắn.
 */
public class ChatPreviewAdapter extends RecyclerView.Adapter<ChatPreviewAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(ChatPreview item);
    }

    private final List<ChatPreview> items;
    private final OnItemClickListener listener;

    public ChatPreviewAdapter(List<ChatPreview> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChatCustomerBinding binding = ItemChatCustomerBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Thêm tin nhắn mới vào đầu danh sách (dùng khi refresh)
    public void addItem(ChatPreview item) {
        items.add(0, item);
        notifyItemInserted(0);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemChatCustomerBinding binding;

        ViewHolder(ItemChatCustomerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ChatPreview item) {
            binding.tvName.setText(item.getWorkerName());
            binding.tvLastMessage.setText(item.getLastMessage());
            binding.tvTime.setText(item.getLastMessageTime());

            // Hiển thị chấm online/offline
            binding.viewOnlineDot.setVisibility(item.isOnline() ? View.VISIBLE : View.GONE);

            // Hiển thị chấm đỏ chưa đọc
            binding.viewUnreadDot.setVisibility(item.isUnread() ? View.VISIBLE : View.GONE);

            // Viền xanh trái: chỉ hiển thị nếu tin nhắn chưa đọc
            binding.viewActiveBorder.setVisibility(item.isUnread() ? View.VISIBLE : View.GONE);

            // Bôi đậm tin nhắn nếu chưa đọc
            binding.tvLastMessage.setTypeface(null,
                    item.isUnread()
                            ? android.graphics.Typeface.BOLD
                            : android.graphics.Typeface.NORMAL);

            // Sự kiện click
            binding.getRoot().setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}
