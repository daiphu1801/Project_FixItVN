package com.fixit.feature.customer.chat.presentation;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.fixit.databinding.ItemMessageReceivedBinding;
import com.fixit.databinding.ItemMessageSentBinding;
import com.fixit.feature.customer.chat.domain.model.ChatMessage;

import java.util.List;

/**
 * Adapter hiển thị các tin nhắn trong màn hình chat.
 * Hỗ trợ 2 ViewType: tin nhắn do mình gửi (SENT) và tin nhắn nhận được (RECEIVED).
 */
public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.BaseViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private final List<ChatMessage> messages;

    public ChatMessageAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isSentByMe() ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_SENT) {
            ItemMessageSentBinding binding = ItemMessageSentBinding.inflate(inflater, parent, false);
            return new SentViewHolder(binding);
        } else {
            ItemMessageReceivedBinding binding = ItemMessageReceivedBinding.inflate(inflater, parent, false);
            return new ReceivedViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.bind(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    /**
     * Thêm tin nhắn mới vào cuối danh sách.
     */
    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    // ============ ViewHolder base class ============
    abstract static class BaseViewHolder extends RecyclerView.ViewHolder {
        BaseViewHolder(@NonNull ViewBinding binding) {
            super(binding.getRoot());
        }
        abstract void bind(ChatMessage message);
    }

    // ============ ViewHolder tin nhắn đã gửi (bên phải, màu xanh) ============
    static class SentViewHolder extends BaseViewHolder {
        private final ItemMessageSentBinding binding;

        SentViewHolder(ItemMessageSentBinding binding) {
            super(binding);
            this.binding = binding;
        }

        @Override
        void bind(ChatMessage message) {
            binding.tvMessage.setText(message.getContent());
            binding.tvTime.setText(message.getTimestamp());
        }
    }

    // ============ ViewHolder tin nhắn nhận được (bên trái, màu trắng) ============
    static class ReceivedViewHolder extends BaseViewHolder {
        private final ItemMessageReceivedBinding binding;

        ReceivedViewHolder(ItemMessageReceivedBinding binding) {
            super(binding);
            this.binding = binding;
        }

        @Override
        void bind(ChatMessage message) {
            binding.tvReceivedMessage.setText(message.getContent());
            binding.tvReceivedTime.setText(message.getTimestamp());
        }
    }
}
