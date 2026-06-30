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

    private ChatViewModel viewModel;
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
        // Khởi tạo ViewModel
        viewModel = new androidx.lifecycle.ViewModelProvider(this).get(ChatViewModel.class);

        // Lấy thông tin thợ/khách được truyền từ màn hình danh sách
        Bundle args = getArguments();
        String workerName = (args != null) ? args.getString("workerName", "Người dùng") : "Người dùng";
        String workerId = (args != null) ? args.getString("workerId", "") : "";

        // Hiển thị tên đối phương trên header
        binding.tvName.setText(workerName);

        // Khởi tạo ViewModel với thông tin người đối thoại
        viewModel.init(workerId, workerName);

        setupRecyclerView();
        setupInputActions();
    }

    private void setupRecyclerView() {
        adapter = new ChatMessageAdapter(messageList);
        layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true); // Cuộn xuống cuối mặc định

        binding.rvChatMessages.setLayoutManager(layoutManager);
        binding.rvChatMessages.setAdapter(adapter);
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

        // Gửi qua ViewModel
        viewModel.sendMessage(text);
        binding.etMessageInput.setText("");
    }

    private void scrollToBottom() {
        if (adapter.getItemCount() > 0) {
            binding.rvChatMessages.scrollToPosition(adapter.getItemCount() - 1);
        }
    }

    @Override
    protected void observeData() {
        // Lắng nghe danh sách tin nhắn thời gian thực từ Firestore
        viewModel.messages.observe(getViewLifecycleOwner(), messages -> {
            messageList.clear();
            if (messages != null) {
                messageList.addAll(messages);
            }
            adapter.notifyDataSetChanged();
            scrollToBottom();
        });

        // Đánh dấu đã đọc khi có tin nhắn mới và Fragment đang hiển thị
        viewModel.messages.observe(getViewLifecycleOwner(), messages -> {
            viewModel.markAsRead();
        });

        // Lắng nghe thông báo lỗi
        viewModel.error.observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                android.widget.Toast.makeText(requireContext(), errorMsg, android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
}
