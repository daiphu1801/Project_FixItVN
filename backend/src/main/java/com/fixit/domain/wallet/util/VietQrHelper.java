package com.fixit.domain.wallet.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class VietQrHelper {

    public String buildVietQrUrl(String bankCode, String accountNumber, String accountName, BigDecimal amount, String transferContent) {
        String encodedAccountName = encode(accountName);
        String encodedContent = encode(transferContent);
        String amountValue = (amount != null ? amount : BigDecimal.ZERO).setScale(0, RoundingMode.UNNECESSARY).toPlainString();

        return "https://img.vietqr.io/image/"
                + bankCode
                + "-"
                + accountNumber
                + "-compact2.png?amount="
                + amountValue
                + "&addInfo="
                + encodedContent
                + "&accountName="
                + encodedAccountName;
    }

    private String encode(String value) {
        return URLEncoder.encode(value != null ? value : "", StandardCharsets.UTF_8);
    }
}
