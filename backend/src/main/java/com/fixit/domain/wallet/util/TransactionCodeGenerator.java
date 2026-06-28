package com.fixit.domain.wallet.util;

import com.fixit.domain.wallet.repository.TransactionHistoryRepository;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class TransactionCodeGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public String generateDepositCode(TransactionHistoryRepository repository) {
        String datePart = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        for (int attempt = 0; attempt < 5; attempt++) {
            int randomPart = SECURE_RANDOM.nextInt(1000, 10000);
            String code = "DEP" + datePart + randomPart;

            if (!repository.existsByTransactionCode(code)) {
                return code;
            }
        }

        return "DEP" + UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase();
    }

    public String generateWithdrawCode(TransactionHistoryRepository repository) {
        String datePart = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        for (int attempt = 0; attempt < 5; attempt++) {
            int randomPart = SECURE_RANDOM.nextInt(1000, 10000);
            String code = "WDR" + datePart + randomPart;

            if (!repository.existsByTransactionCode(code)) {
                return code;
            }
        }

        return "WDR" + UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase();
    }
}
