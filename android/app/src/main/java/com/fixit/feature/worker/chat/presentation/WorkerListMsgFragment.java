package com.fixit.feature.worker.chat.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentListMsgBinding;
import com.fixit.databinding.ItemChatWorkerBinding;
import com.fixit.feature.customer.chat.domain.model.ChatPreview;
import com.fixit.feature.customer.chat.presentation.ConversationsViewModel;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Màn hình danh sách hội thoại phía thợ.
 * Dùng shared layout fragment_list_msg.xml và FragmentListMsgBinding.
 * isTopLevel=true (default) vì đây là tab bottom nav → ẩn nút back.
 */
@AndroidEntryPoint
public class WorkerListMsgFragment extends BaseFragment<FragmentListMsgBinding> {

    private ConversationsViewModel viewModel;
    private final List<ChatPreview> conversationList = new ArrayList<>();
    private WorkerChatPreviewAdapter adapter;

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

        // Cấu hình sự kiện cho nút FAB (dấu cộng) để mở chat với khách hàng mẫu phục vụ việc test
        binding.fabNewChat.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("workerId", "customer_b_id"); // key giữ nguyên làm receiverId trong ChatCustomerFragment
            args.putString("workerName", "Trần Thị B (Khách hàng Test)");
            if (navController != null) {
                navController.navigate(R.id.action_worker_list_msg_to_chat, args);
            }
        });
    }

    /**
     * isTopLevel=true  (Worker, tab bottom nav) → ẩn nút trái
     * isTopLevel=false (trường hợp dự phòng) → hiện back arrow
     */
    private void configureTopBar() {
        boolean isTopLevel = getArguments() == null
                || getArguments().getBoolean("isTopLevel", true);

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
        adapter = new WorkerChatPreviewAdapter(conversationList, this::onConversationClicked);

        binding.rvChats.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvChats.setAdapter(adapter);
        binding.rvChats.setNestedScrollingEnabled(false);
    }

    private void onConversationClicked(ChatPreview item) {
        // Truyền tên khách hàng sang màn hình chat (dùng chung ChatCustomerFragment)
        Bundle args = new Bundle();
        args.putString("workerName", item.getWorkerName()); // key giữ nguyên để tái sử dụng fragment
        args.putString("workerId", item.getWorkerId());

        if (navController != null) {
            navController.navigate(R.id.action_worker_list_msg_to_chat, args);
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

    // ============ Inner Adapter dùng ItemChatWorkerBinding ============
    private static class WorkerChatPreviewAdapter
            extends RecyclerView.Adapter<WorkerChatPreviewAdapter.ViewHolder> {

        interface OnItemClickListener {
            void onItemClick(ChatPreview item);
        }

        private final List<ChatPreview> items;
        private final OnItemClickListener listener;

        WorkerChatPreviewAdapter(List<ChatPreview> items, OnItemClickListener listener) {
            this.items = items;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemChatWorkerBinding binding = ItemChatWorkerBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(items.get(position), listener);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemChatWorkerBinding binding;

            ViewHolder(ItemChatWorkerBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            void bind(ChatPreview item, OnItemClickListener listener) {
                binding.tvName.setText(item.getWorkerName());
                binding.tvLastMessage.setText(item.getLastMessage());
                binding.tvTime.setText(item.getLastMessageTime());

                binding.viewOnlineDot.setVisibility(item.isOnline() ? View.VISIBLE : View.GONE);
                binding.viewUnreadDot.setVisibility(item.isUnread() ? View.VISIBLE : View.GONE);
                binding.viewActiveBorder.setVisibility(item.isUnread() ? View.VISIBLE : View.GONE);
                binding.tvLastMessage.setTypeface(null,
                        item.isUnread()
                                ? android.graphics.Typeface.BOLD
                                : android.graphics.Typeface.NORMAL);

                binding.getRoot().setOnClickListener(v -> listener.onItemClick(item));
            }
        }
    }
}
