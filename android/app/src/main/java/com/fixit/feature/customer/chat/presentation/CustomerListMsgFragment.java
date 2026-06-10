package com.fixit.feature.customer.chat.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentListMsgBinding;
import com.fixit.feature.customer.chat.domain.model.ChatPreview;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Màn hình danh sách tất cả hội thoại của khách hàng.
 * Dùng shared layout fragment_list_msg.xml.
 * Nhận argument isTopLevel:
 *   - false (default): được navigate vào từ icon chat trang chủ → hiện nút back
 *   - true: là top-level tab → ẩn nút back (dự phòng cho tương lai)
 */
@AndroidEntryPoint
public class CustomerListMsgFragment extends BaseFragment<FragmentListMsgBinding> {

    private ChatPreviewAdapter adapter;
    private ConversationsViewModel viewModel;
    private final List<ChatPreview> conversationList = new ArrayList<>();

    @NonNull
    @Override
    protected FragmentListMsgBinding inflateViewBinding(
            @NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentListMsgBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        viewModel = new androidx.lifecycle.ViewModelProvider(this).get(ConversationsViewModel.class);
        configureTopBar();
        setupRecyclerView();
        viewModel.startListening();

        // Cấu hình sự kiện cho nút FAB (dấu cộng) để mở chat với thợ mẫu phục vụ việc test
        binding.fabNewChat.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("workerId", "worker_tuan_123");
            args.putString("workerName", "Anh Tuấn - Thợ Điện");
            if (navController != null) {
                navController.navigate(R.id.action_list_msg_to_chat, args);
            }
        });
    }

    /**
     * Cấu hình nút trái header dựa vào isTopLevel:
     *   isTopLevel=false → back arrow + popBackStack (customer navigate từ home)
     *   isTopLevel=true  → GONE (top-level tab, không cần back)
     */
    private void configureTopBar() {
        boolean isTopLevel = getArguments() != null
                && getArguments().getBoolean("isTopLevel", false);

        if (isTopLevel) {
            binding.btnMenu.setVisibility(View.GONE);
        } else {
            binding.btnMenu.setVisibility(View.VISIBLE);
            binding.btnMenu.setImageResource(R.drawable.ic_lucide_arrow_left);
            binding.btnMenu.setOnClickListener(v -> {
                if (navController != null) navController.popBackStack();
            });
        }
    }

    private void setupRecyclerView() {
        adapter = new ChatPreviewAdapter(conversationList, this::onConversationClicked);

        binding.rvChats.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvChats.setAdapter(adapter);
        binding.rvChats.setNestedScrollingEnabled(false);
    }

    private void onConversationClicked(ChatPreview item) {
        Bundle args = new Bundle();
        args.putString("workerName", item.getWorkerName());
        args.putString("workerId", item.getWorkerId());

        if (navController != null) {
            navController.navigate(R.id.action_list_msg_to_chat, args);
        }
    }

    @Override
    protected void observeData() {
        viewModel.conversations.observe(getViewLifecycleOwner(), list -> {
            conversationList.clear();
            if (list != null) {
                conversationList.addAll(list);
            }
            adapter.notifyDataSetChanged();
        });

        viewModel.error.observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                android.widget.Toast.makeText(requireContext(), errorMsg, android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
}
