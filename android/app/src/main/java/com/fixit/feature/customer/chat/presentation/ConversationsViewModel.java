package com.fixit.feature.customer.chat.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fixit.core.storage.SessionStorage;
import com.fixit.feature.auth.domain.model.Session;
import com.fixit.feature.customer.chat.domain.model.ChatPreview;
import com.fixit.feature.customer.chat.domain.repository.ChatRepository;
import com.fixit.feature.customer.chat.domain.usecase.GetConversationsUseCase;
import com.fixit.feature.customer.booking.domain.usecase.GetBookingsUseCase;
import com.fixit.feature.worker.orders.domain.usecase.GetWorkerOrdersUseCase;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ConversationsViewModel extends ViewModel {

    private final GetConversationsUseCase getConversationsUseCase;
    private final SessionStorage sessionStorage;
    private final GetBookingsUseCase getBookingsUseCase;
    private final GetWorkerOrdersUseCase getWorkerOrdersUseCase;

    private final MutableLiveData<List<ChatPreview>> _conversations = new MutableLiveData<>();
    public final LiveData<List<ChatPreview>> conversations = _conversations;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public final LiveData<String> error = _error;

    private ListenerRegistration listenerRegistration;

    // Quản lý dữ liệu lọc và tìm kiếm cục bộ
    private final List<ChatPreview> rawConversations = new ArrayList<>();
    private final List<String> activePartnerIds = new ArrayList<>();
    private String searchQuery = "";
    private String filterType = "ALL"; // "ALL", "UNREAD", "ACTIVE_JOB"

    @Inject
    public ConversationsViewModel(GetConversationsUseCase getConversationsUseCase,
                                  SessionStorage sessionStorage,
                                  GetBookingsUseCase getBookingsUseCase,
                                  GetWorkerOrdersUseCase getWorkerOrdersUseCase) {
        this.getConversationsUseCase = getConversationsUseCase;
        this.sessionStorage = sessionStorage;
        this.getBookingsUseCase = getBookingsUseCase;
        this.getWorkerOrdersUseCase = getWorkerOrdersUseCase;
    }

    public void startListening() {
        Session session = sessionStorage.getSession();
        if (session == null || session.getUser() == null) {
            _error.setValue("Bạn chưa đăng nhập");
            return;
        }

        String userId = session.getUser().getId();
        String userRole = session.getUser().getRole().name();

        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }

        fetchActiveBookings();

        listenerRegistration = getConversationsUseCase.execute(userId, userRole, new ChatRepository.ConversationsListener() {
            @Override
            public void onConversationsUpdated(List<ChatPreview> list) {
                rawConversations.clear();
                if (list != null) {
                    rawConversations.addAll(list);
                }
                applyFilter();
            }

            @Override
            public void onError(Exception e) {
                _error.setValue(e.getMessage());
            }
        });
    }

    public void setSearchQuery(String query) {
        this.searchQuery = query;
        applyFilter();
    }

    public void setFilterType(String type) {
        this.filterType = type;
        applyFilter();
    }

    private void fetchActiveBookings() {
        Session session = sessionStorage.getSession();
        if (session == null || session.getUser() == null) return;
        String role = session.getUser().getRole().name();

        if ("CUSTOMER".equalsIgnoreCase(role)) {
            getBookingsUseCase.execute(result -> {
                if (result != null && result.isSuccess() && result.getData() != null) {
                    activePartnerIds.clear();
                    for (com.fixit.feature.customer.booking.domain.model.CustomerBooking booking : result.getData()) {
                        String status = booking.getStatus() != null ? booking.getStatus().toLowerCase() : "";
                        if (!"completed".equals(status) && !"cancelled".equals(status)) {
                            if (booking.getWorker() != null && booking.getWorker().getWorkerId() != null) {
                                activePartnerIds.add(booking.getWorker().getWorkerId());
                            }
                        }
                    }
                    applyFilter();
                }
            });
        } else if ("WORKER".equalsIgnoreCase(role)) {
            getWorkerOrdersUseCase.execute(result -> {
                if (result != null && result.isSuccess() && result.getData() != null) {
                    activePartnerIds.clear();
                    for (com.fixit.feature.worker.orders.domain.model.WorkerOrder order : result.getData()) {
                        String status = order.getStatus() != null ? order.getStatus().toLowerCase() : "";
                        if (!"completed".equals(status) && !"cancelled".equals(status)) {
                            if (order.getCustomerId() != null) {
                                activePartnerIds.add(order.getCustomerId());
                            }
                        }
                    }
                    applyFilter();
                }
            });
        }
    }

    private void applyFilter() {
        List<ChatPreview> filtered = new ArrayList<>();
        for (ChatPreview item : rawConversations) {
            // Lọc theo từ khóa tìm kiếm
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String q = searchQuery.toLowerCase().trim();
                String name = item.getWorkerName() != null ? item.getWorkerName().toLowerCase() : "";
                String lastMsg = item.getLastMessage() != null ? item.getLastMessage().toLowerCase() : "";
                if (!name.contains(q) && !lastMsg.contains(q)) {
                    continue;
                }
            }

            // Lọc theo loại tab (Tất cả / Chưa đọc / Việc đang làm)
            if ("UNREAD".equals(filterType)) {
                if (!item.isUnread()) {
                    continue;
                }
            } else if ("ACTIVE_JOB".equals(filterType)) {
                if (!activePartnerIds.contains(item.getWorkerId())) {
                    continue;
                }
            }

            filtered.add(item);
        }
        _conversations.setValue(filtered);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }
}
