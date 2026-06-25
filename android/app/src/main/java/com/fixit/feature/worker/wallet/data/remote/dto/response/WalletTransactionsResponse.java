// PATH: android/app/src/main/java/com/fixit/feature/worker/wallet/data/remote/dto/response/WalletTransactionsResponse.java

package com.fixit.feature.worker.wallet.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WalletTransactionsResponse {

    @SerializedName("workerId")
    private String workerId;

    @SerializedName("type")
    private String type;

    @SerializedName("page")
    private int page;

    @SerializedName("size")
    private int size;

    @SerializedName("totalElements")
    private long totalElements;

    @SerializedName("totalPages")
    private int totalPages;

    @SerializedName("hasNext")
    private boolean hasNext;

    @SerializedName("transactions")
    private List<WalletTransactionItemResponse> transactions;

    public String getWorkerId() {
        return workerId;
    }

    public String getType() {
        return type;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public List<WalletTransactionItemResponse> getTransactions() {
        return transactions;
    }
}
