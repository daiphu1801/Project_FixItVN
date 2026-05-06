package com.fixit.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class ViewUtils {

    // Format tiền VND (e.g. 1500000 -> "1.500.000 ₫")
    public static String formatCurrency(long amount) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(amount) + " ₫";
    }

    // Format phần trăm chiết khấu (e.g. 0.15 -> "15%")
    public static String formatPercent(double rate) {
        return (int)(rate * 100) + "%";
    }
}
