package com.example.untungku;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.example.untungku.data.entity.Expense;
import com.example.untungku.data.entity.Product;
import com.example.untungku.data.entity.Sale;
import com.example.untungku.data.local.AppDatabase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ReportExporter {
    public static void exportDataToCSV(Context context) {
        try {
            AppDatabase db = AppDatabase.getDatabase(context);
            List<Product> products = db.productDao().getAllProducts();
            List<Sale> sales = db.saleDao().getAllSales();
            List<Expense> expenses = db.expenseDao().getAllExpenses();

            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }

            File file = new File(downloadsDir, "Laporan_UntungKu_" + System.currentTimeMillis() + ".csv");
            FileWriter writer = new FileWriter(file);

            writer.append("=== LAPORAN PRODUK ===\n");
            writer.append("Nama Produk,Stok,Harga Modal,Harga Jual\n");
            for (Product p : products) {
                writer.append(escapeCsv(p.nama_produk)).append(",")
                      .append(String.valueOf(p.stok)).append(",")
                      .append(String.valueOf(p.harga_modal)).append(",")
                      .append(String.valueOf(p.harga_jual)).append("\n");
            }

            writer.append("\n=== LAPORAN PENJUALAN ===\n");
            writer.append("ID Penjualan,Total Harga,Keuntungan\n");
            for (Sale s : sales) {
                writer.append(String.valueOf(s.id_penjualan)).append(",")
                      .append(String.valueOf(s.total_harga)).append(",")
                      .append(String.valueOf(s.keuntungan)).append("\n");
            }

            writer.append("\n=== LAPORAN PENGELUARAN ===\n");
            writer.append("Keterangan,Nominal\n");
            for (Expense e : expenses) {
                writer.append(escapeCsv(e.keterangan)).append(",")
                      .append(String.valueOf(e.nominal)).append("\n");
            }

            writer.flush();
            writer.close();

            Toast.makeText(context, "Laporan tersimpan di Downloads: " + file.getName(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(context, "Gagal menyimpan laporan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        return value;
    }
}
