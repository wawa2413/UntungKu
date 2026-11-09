package com.example.untungku.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.untungku.CurrencyFormat;
import com.example.untungku.R;
import com.example.untungku.data.entity.Product;
import com.example.untungku.data.entity.Sale;
import com.example.untungku.data.entity.SaleDetail;
import com.example.untungku.data.local.AppDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SaleActivity extends AppCompatActivity {

    private Spinner spinnerProducts;
    private EditText etJumlahJual;
    private TextView tvHargaSatuan, tvTotalBayar;
    private Button btnPlus, btnMinus, btnSimpanPenjualan;
    private AppDatabase db;
    private List<Product> productList;
    private List<String> productNames;
    private Product selectedProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);

        db = AppDatabase.getDatabase(this);

        spinnerProducts = findViewById(R.id.spinnerProducts);
        etJumlahJual = findViewById(R.id.etJumlahJual);
        tvHargaSatuan = findViewById(R.id.tvHargaSatuan);
        tvTotalBayar = findViewById(R.id.tvTotalBayar);
        btnPlus = findViewById(R.id.btnPlus);
        btnMinus = findViewById(R.id.btnMinus);
        btnSimpanPenjualan = findViewById(R.id.btnSimpanPenjualan);

        loadProductSpinner();

        spinnerProducts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!productList.isEmpty()) {
                    selectedProduct = productList.get(position);
                    tvHargaSatuan.setText("Harga Jual: " + CurrencyFormat.formatRupiah(selectedProduct.harga_jual) + " | Stok: " + selectedProduct.stok);
                    calculateTotal();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnPlus.setOnClickListener(v -> {
            int current = getQtyInput();
            etJumlahJual.setText(String.valueOf(current + 1));
        });

        btnMinus.setOnClickListener(v -> {
            int current = getQtyInput();
            if (current > 1) {
                etJumlahJual.setText(String.valueOf(current - 1));
            }
        });

        etJumlahJual.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateTotal();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSimpanPenjualan.setOnClickListener(v -> processSale());
    }

    private void loadProductSpinner() {
        productList = db.productDao().getAllProducts();
        productNames = new ArrayList<>();

        for (Product p : productList) {
            productNames.add(p.nama_produk);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, productNames);
        spinnerProducts.setAdapter(adapter);
    }

    private int getQtyInput() {
        String val = etJumlahJual.getText().toString().trim();
        if (val.isEmpty()) return 1;
        try {
            return Integer.parseInt(val);
        } catch (Exception e) {
            return 1;
        }
    }

    private void calculateTotal() {
        if (selectedProduct == null) return;
        int qty = getQtyInput();
        double total = selectedProduct.harga_jual * qty;
        tvTotalBayar.setText(CurrencyFormat.formatRupiah(total));
    }

    private void processSale() {
        if (productList.isEmpty() || selectedProduct == null) {
            Toast.makeText(this, "Pilih produk terlebih dahulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        int qty = getQtyInput();
        if (qty > selectedProduct.stok) {
            Toast.makeText(this, "Stok tidak mencukupi! Sisa stok: " + selectedProduct.stok, Toast.LENGTH_SHORT).show();
            return;
        }

        double totalPenjualan = selectedProduct.harga_jual * qty;
        double totalModal = selectedProduct.harga_modal * qty;
        double keuntunganKotor = totalPenjualan - totalModal;

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        Sale sale = new Sale();
        sale.tanggal = currentDate;
        sale.total_penjualan = totalPenjualan;
        sale.total_modal = totalModal;
        sale.keuntungan_kotor = keuntunganKotor;
        long saleId = db.saleDao().insertSale(sale);

        SaleDetail detail = new SaleDetail();
        detail.id_penjualan = (int) saleId;
        detail.id_produk = selectedProduct.id_produk;
        detail.jumlah = qty;
        detail.harga_modal = selectedProduct.harga_modal;
        detail.harga_jual = selectedProduct.harga_jual;
        detail.subtotal = totalPenjualan;
        db.saleDao().insertSaleDetail(detail);

        db.productDao().updateStock(selectedProduct.id_produk, qty);

        Toast.makeText(this, "Transaksi Berhasil! Stok otomatis berkurang.", Toast.LENGTH_SHORT).show();
        finish();
    }
}