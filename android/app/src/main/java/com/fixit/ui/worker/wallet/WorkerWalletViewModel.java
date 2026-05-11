package com.fixit.ui.worker.wallet;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fixit.base.BaseViewModel;
import com.fixit.data.model.WalletTransaction;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkerWalletViewModel extends BaseViewModel {

    // ── Mock số dư 3 loại ví ────────────────────────────────────────────────
    private final MutableLiveData<String> _availableBalance = new MutableLiveData<>("1.250.000 đ");
    private final MutableLiveData<String> _heldBalance      = new MutableLiveData<>("320.000 đ");
    private final MutableLiveData<String> _debtBalance      = new MutableLiveData<>("75.000 đ");

    public LiveData<String> availableBalance = _availableBalance;
    public LiveData<String> heldBalance      = _heldBalance;
    public LiveData<String> debtBalance      = _debtBalance;

    // ── Mock giao dịch ──────────────────────────────────────────────────────
    private static final List<WalletTransaction> ALL_TX = Arrays.asList(
            // Ví khả dụng
            new WalletTransaction("Nhận tiền đơn ORD003 (chuyển khoản)",
                    "08/05/2026 - 14:30", "350.000 đ", true,  "available"),
            new WalletTransaction("Rút tiền về Vietcombank",
                    "07/05/2026 - 09:00", "500.000 đ", false, "available"),
            new WalletTransaction("Nhận tiền đơn ORD001",
                    "06/05/2026 - 17:00", "150.000 đ", true,  "available"),

            // Ví tạm giữ
            new WalletTransaction("Giữ bảo hành đơn ORD003",
                    "08/05/2026 - 14:30", "100.000 đ", false, "held"),
            new WalletTransaction("Giải phóng bảo hành ORD002",
                    "05/05/2026 - 11:00", "80.000 đ",  true,  "held"),
            new WalletTransaction("Giữ bảo hành đơn ORD001",
                    "06/05/2026 - 17:00", "50.000 đ",  false, "held"),

            // Ví ghi nợ
            new WalletTransaction("Chiết khấu đơn tiền mặt ORD004",
                    "07/05/2026 - 16:00", "30.000 đ",  false, "debt"),
            new WalletTransaction("Nạp tiền trả nợ",
                    "06/05/2026 - 08:00", "100.000 đ", true,  "debt"),
            new WalletTransaction("Chiết khấu đơn tiền mặt ORD002",
                    "05/05/2026 - 15:30", "45.000 đ",  false, "debt")
    );

    private final MutableLiveData<List<WalletTransaction>> _filteredTx = new MutableLiveData<>();
    public LiveData<List<WalletTransaction>> filteredTransactions = _filteredTx;

    @Inject
    public WorkerWalletViewModel() {
        filterByWallet("available"); // Mặc định tab Khả dụng
    }

    /**
     * @param walletType "available" | "held" | "debt" | "all"
     */
    public void filterByWallet(String walletType) {
        if ("all".equals(walletType)) {
            _filteredTx.setValue(ALL_TX);
        } else {
            _filteredTx.setValue(
                    ALL_TX.stream()
                          .filter(t -> walletType.equals(t.getWalletType()))
                          .collect(Collectors.toList())
            );
        }
    }
}
