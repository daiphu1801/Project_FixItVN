package com.fixit.feature.customer.chat.presentation;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentChatCustomerBinding;
import com.fixit.feature.customer.chat.domain.model.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Màn hình chat 1-1 giữa khách hàng và thợ.
 * Nhận workerName qua Bundle từ CustomerListMsgFragment.
 * Hỗ trợ gửi tin nhắn mới (hiển thị ngay phía dưới list).
 */
@AndroidEntryPoint
public class ChatCustomerFragment extends BaseFragment<FragmentChatCustomerBinding> {

    private ChatMessageAdapter adapter;
    private final List<ChatMessage> messageList = new ArrayList<>();
    private LinearLayoutManager layoutManager;

    @NonNull
    @Override
    protected FragmentChatCustomerBinding inflateViewBinding(
            @NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentChatCustomerBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        // Lấy tên thợ được truyền từ màn hình danh sách
        Bundle args = getArguments();
        String workerName = (args != null) ? args.getString("workerName", "Thợ") : "Thợ";

        // Hiển thị tên thợ trên header
        binding.tvName.setText(workerName);

        setupRecyclerView();
        setupInputActions();
    }

    private void setupRecyclerView() {
        // Nạp tin nhắn giả lập
        messageList.addAll(buildFakeMessages());

        adapter = new ChatMessageAdapter(messageList);
        layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true); // Cuộn xuống cuối mặc định

        binding.rvChatMessages.setLayoutManager(layoutManager);
        binding.rvChatMessages.setAdapter(adapter);

        // Cuộn tới tin nhắn cuối cùng
        scrollToBottom();
    }

    private void setupInputActions() {
        // Nút Back
        binding.btnBack.setOnClickListener(v -> {
            if (navController != null) navController.popBackStack();
        });

        // Nút Gửi
        binding.btnSend.setOnClickListener(v -> sendMessage());

        // Cho phép nhấn "Done" / Enter trên bàn phím để gửi
        binding.etMessageInput.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                return true;
            }
            return false;
        });
    }

    private void sendMessage() {
        String text = binding.etMessageInput.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;

        // Lấy giờ hiện tại làm timestamp
        String timestamp = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        // Tạo tin nhắn mới (isSentByMe = true)
        ChatMessage newMessage = new ChatMessage(
                UUID.randomUUID().toString(),
                text,
                timestamp,
                true
        );

        // Thêm vào adapter và cuộn xuống
        adapter.addMessage(newMessage);
        binding.etMessageInput.setText("");
        scrollToBottom();
    }

    private void scrollToBottom() {
        if (adapter.getItemCount() > 0) {
            binding.rvChatMessages.scrollToPosition(adapter.getItemCount() - 1);
        }
    }

    @Override
    protected void observeData() {
        // Sẽ kết nối ViewModel/WebSocket thực ở đây sau
    }

    // ============ Fake data ============
    private List<ChatMessage> buildFakeMessages() {
        List<ChatMessage> list = new ArrayList<>();
        list.add(new ChatMessage("1",
                "Chào chị, em là thợ được phân công cho đơn hàng sửa tủ lạnh của chị ạ.",
                "08:30", false));
        list.add(new ChatMessage("2",
                "Chào anh! Anh có thể đến sớm không?",
                "08:32", true));
        list.add(new ChatMessage("3",
                "Dạ em đang trên đường rồi ạ, khoảng 15 phút nữa em đến!",
                "08:33", false));
        list.add(new ChatMessage("4",
                "Ok anh nhé, em mở cửa sẵn cho.",
                "08:35", true));
        list.add(new ChatMessage("5",
                "Vâng ạ! Em cảm ơn chị 😊",
                "08:35", false));
        list.add(new ChatMessage("6",
                "Em sẽ có mặt trong 15 phút ạ!",
                "08:47", false));
        return list;
    }
}
