package com.example.untungku.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.untungku.R;
import com.example.untungku.data.entity.Expense;
import com.example.untungku.data.local.AppDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExpenseActivity extends AppCompatActivity {

    private EditText etKategori, etKeterangan, etNominal;
    private Button btnSimpanPengeluaran;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        db = AppDatabase.getDatabase(this);

        etKategori = findViewById(R.id.etKategori);
        etKeterangan = findViewById(R.id.etKeterangan);
        etNominal = findViewById(R.id.etNominal);
        btnSimpanPengeluaran = findViewById(R.id.btnSimpanPengeluaran);

        btnSimpanPengeluaran.setOnClickListener(v -> saveExpense());
    }

    private void saveExpense() {
        String kategori = etKategori.getText().toString().trim();
        String keterangan = etKeterangan.getText().toString().trim();
        String nominalStr = etNominal.getText().toString().trim();

        if (kategori.isEmpty() || nominalStr.isEmpty()) {
            Toast.makeText(this, "Kategori dan Nominal wajib diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        double nominal = Double.parseDouble(nominalStr);
        if (nominal <= 0) {
            Toast.makeText(this, "Nominal pengeluaran harus lebih dari 0!", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        Expense expense = new Expense();
        expense.tanggal = currentDate;
        expense.kategori = kategori;
        expense.keterangan = keterangan.isEmpty() ? "-" : keterangan;
        expense.nominal = nominal;

        db.expenseDao().insertExpense(expense);
        Toast.makeText(this, "Pengeluaran berhasil dicatat!", Toast.LENGTH_SHORT).show();
        finish(); // Kembali ke Dashboard
    }
}