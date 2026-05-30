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

    @NonNull
    @Override
    protected FragmentListMsgBinding inflateViewBinding(
            @NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentListMsgBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        configureTopBar();
        setupRecyclerView();
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
        List<ChatPreview> fakeData = buildFakeConversations();
        adapter = new ChatPreviewAdapter(fakeData, this::onConversationClicked);

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
        // Sẽ kết nối ViewModel/API thực ở đây sau
    }

    private List<ChatPreview> buildFakeConversations() {
        List<ChatPreview> list = new ArrayList<>();
        list.add(new ChatPreview("w1", "Anh Tuấn — Thợ Điện",
                "Em sẽ có mặt trong 15 phút ạ!", "Vừa xong", true, true));
        list.add(new ChatPreview("w2", "Chú Hùng — Thợ Máy Lạnh",
                "Chị cần em bơm thêm gas không ạ?", "10:45", true, false));
        list.add(new ChatPreview("w3", "Anh Khoa — Thợ Ống Nước",
                "Em đã sửa xong rồi ạ, chị kiểm tra lại nhé.", "Hôm qua", false, false));
        list.add(new ChatPreview("w4", "Chú Minh — Thợ Điện Lạnh",
                "Tủ lạnh của chị cần thay lốc mới ạ.", "T4", false, false));
        list.add(new ChatPreview("w5", "Anh Phong — Thợ Bếp",
                "Bếp từ của chị ok rồi ạ!", "T2", false, false));
        return list;
    }
}
