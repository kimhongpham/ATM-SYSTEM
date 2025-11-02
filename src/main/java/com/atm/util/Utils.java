package com.atm.util;

import java.text.NumberFormat;
import java.util.Locale;

public class Utils {
    public static String formatCurrency(double amount) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return currencyFormatter.format(amount);
    }

}
