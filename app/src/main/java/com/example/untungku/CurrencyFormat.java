package com.example.untungku;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyFormat {
    public static String formatRupiah(double nominal) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        // Mengubah format agar menjadi "Rp 50.000" tanpa tanda koma desimal di belakang
        String hasil = formatRupiah.format(nominal);
        return hasil.replace(",00", "");
    }
}