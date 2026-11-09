package com.example.untungku;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.untungku.data.local.AppDatabase;
import com.example.untungku.data.entity.Sale;
import com.example.untungku.data.entity.Product;
import com.example.untungku.data.entity.Expense;
import com.example.untungku.ui.ProductAdapter;
import com.example.untungku.ui.HistoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvTotalPenjualan, tvTotalKeuntungan, btnLogout;
    private TextView menuProduk, menuPenjualan, menuPengeluaran, menuRiwayat;
    private CardView cardKontenDinamis;
    private TextView tvJudulKonten;
    private Button btnAksiTambah, btnSimpanForm;
    private RecyclerView rvKontenList;
    private LinearLayout layoutFormInput;
    private Spinner spinnerProduk;
    private EditText etFormJumlah, etFormKeterangan, etFormNominal;
    private ImageButton btnUnduhLaporan;
    private AppDatabase db;
    private ProductAdapter productAdapter;
    private HistoryAdapter historyAdapter;
    private List<Product> productList = new ArrayList<>();

    private String selectedImageUri = "";
    private ImageView ivDialogPreview;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getDatabase(this);

        tvTotalPenjualan = findViewById(R.id.tvTotalPenjualan);
        tvTotalKeuntungan = findViewById(R.id.tvTotalKeuntungan);
        btnUnduhLaporan = findViewById(R.id.btnUnduhLaporan);
        btnLogout = findViewById(R.id.btnLogout);

        menuProduk = findViewById(R.id.menuProduk);
        menuPenjualan = findViewById(R.id.menuPenjualan);
        menuPengeluaran = findViewById(R.id.menuPengeluaran);
        menuRiwayat = findViewById(R.id.menuRiwayat);

        cardKontenDinamis = findViewById(R.id.cardKontenDinamis);
        tvJudulKonten = findViewById(R.id.tvJudulKonten);
        btnAksiTambah = findViewById(R.id.btnAksiTambah);
        rvKontenList = findViewById(R.id.rvKontenList);
        layoutFormInput = findViewById(R.id.layoutFormInput);

        spinnerProduk = findViewById(R.id.spinnerProduk);
        etFormJumlah = findViewById(R.id.etFormJumlah);
        etFormKeterangan = findViewById(R.id.etFormKeterangan);
        etFormNominal = findViewById(R.id.etFormNominal);
        btnSimpanForm = findViewById(R.id.btnSimpanForm);

        rvKontenList.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(productList);
        rvKontenList.setAdapter(productAdapter);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            try {
                                getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            } catch (Exception ignored) {}
                            selectedImageUri = imageUri.toString();
                            if (ivDialogPreview != null) {
                                ivDialogPreview.setImageURI(imageUri);
                            }
                        }
                    }
                }
        );

        // Default awal: Buka menu Kelola Produk
        bukaMenuKelolaProduk();

        View.OnClickListener menuClickListener = v -> {
            menuProduk.setSelected(false);
            menuProduk.setTextColor(getColor(android.R.color.black));
            menuPenjualan.setSelected(false);
            menuPenjualan.setTextColor(getColor(android.R.color.black));
            menuPengeluaran.setSelected(false);
            menuPengeluaran.setTextColor(getColor(android.R.color.black));
            menuRiwayat.setSelected(false);
            menuRiwayat.setTextColor(getColor(android.R.color.black));

            v.setSelected(true);
            ((TextView) v).setTextColor(getColor(android.R.color.white));

            if (v.getId() == R.id.menuProduk) {
                bukaMenuKelolaProduk();
            } else if (v.getId() == R.id.menuPenjualan) {
                bukaMenuCatatPenjualan();
            } else if (v.getId() == R.id.menuPengeluaran) {
                bukaMenuPengeluaran();
            } else if (v.getId() == R.id.menuRiwayat) {
                bukaMenuRiwayat();
            }
        };

        menuProduk.setOnClickListener(menuClickListener);
        menuPenjualan.setOnClickListener(menuClickListener);
        menuPengeluaran.setOnClickListener(menuClickListener);
        menuRiwayat.setOnClickListener(menuClickListener);

        menuProduk.setSelected(true);
        menuProduk.setTextColor(getColor(android.R.color.white));

        btnUnduhLaporan.setOnClickListener(v -> {
            ReportExporter.exportDataToCSV(this);
        });

        btnLogout.setOnClickListener(v -> finish());
    }

    private void bukaMenuKelolaProduk() {
        tvJudulKonten.setText("Kelola Produk & Stok");
        btnAksiTambah.setVisibility(View.VISIBLE);
        btnAksiTambah.setText("+ Tambah Produk");
        rvKontenList.setVisibility(View.VISIBLE);
        layoutFormInput.setVisibility(View.GONE);

        rvKontenList.setAdapter(productAdapter);
        loadProductsData();
        btnAksiTambah.setOnClickListener(v -> showAddProductDialog());
    }

    private void bukaMenuCatatPenjualan() {
        tvJudulKonten.setText("Form Catat Penjualan (POS)");
        btnAksiTambah.setVisibility(View.GONE);
        rvKontenList.setVisibility(View.GONE);
        layoutFormInput.setVisibility(View.VISIBLE);

        spinnerProduk.setVisibility(View.VISIBLE);
        etFormJumlah.setVisibility(View.VISIBLE);
        etFormKeterangan.setVisibility(View.GONE);
        etFormNominal.setVisibility(View.GONE);
        btnSimpanForm.setText("Simpan Transaksi Penjualan");

        loadProductSpinner();

        btnSimpanForm.setOnClickListener(v -> {
            try {
                if (productList.isEmpty()) {
                    Toast.makeText(this, "Belum ada produk! Tambahkan produk terlebih dahulu.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int selectedPosition = spinnerProduk.getSelectedItemPosition();
                Product selectedProduct = productList.get(selectedPosition);

                String jumlahStr = etFormJumlah.getText().toString().trim();
                if (jumlahStr.isEmpty()) {
                    Toast.makeText(this, "Masukkan jumlah terjual!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int qtySold = Integer.parseInt(jumlahStr);
                if (qtySold <= 0) {
                    Toast.makeText(this, "Jumlah harus lebih dari 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (qtySold > selectedProduct.stok) {
                    Toast.makeText(this, "Stok tidak mencukupi! Sisa stok: " + selectedProduct.stok, Toast.LENGTH_SHORT).show();
                    return;
                }

                double totalHarga = selectedProduct.harga_jual * qtySold;
                double totalModal = selectedProduct.harga_modal * qtySold;
                double keuntunganKotor = totalHarga - totalModal;

                Sale sale = new Sale();
                sale.total_harga = totalHarga;
                sale.keuntungan = keuntunganKotor;
                db.saleDao().insertSale(sale);

                selectedProduct.stok -= qtySold;
                db.productDao().updateProduct(selectedProduct);

                Toast.makeText(this, "Penjualan berhasil dicatat! Keuntungan: " + CurrencyFormat.formatRupiah(keuntunganKotor), Toast.LENGTH_LONG).show();

                etFormJumlah.setText("");
                loadDashboardSummary();
                loadProductsData();

            } catch (Exception e) {
                Toast.makeText(this, "Gagal mencatat penjualan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bukaMenuPengeluaran() {
        tvJudulKonten.setText("Catat Pengeluaran Usaha");
        btnAksiTambah.setVisibility(View.GONE);
        rvKontenList.setVisibility(View.GONE);
        layoutFormInput.setVisibility(View.VISIBLE);

        spinnerProduk.setVisibility(View.GONE);
        etFormJumlah.setVisibility(View.GONE);
        etFormKeterangan.setVisibility(View.VISIBLE);
        etFormNominal.setVisibility(View.VISIBLE);
        btnSimpanForm.setText("Simpan Pengeluaran");

        btnSimpanForm.setOnClickListener(v -> {
            try {
                String ket = etFormKeterangan.getText().toString().trim();
                String nominalStr = etFormNominal.getText().toString().trim();

                if (ket.isEmpty() || nominalStr.isEmpty()) {
                    Toast.makeText(this, "Keterangan dan nominal wajib diisi!", Toast.LENGTH_SHORT).show();
                    return;
                }

                double nominal = Double.parseDouble(nominalStr);

                Expense expense = new Expense();
                expense.keterangan = ket;
                expense.nominal = nominal;
                db.expenseDao().insertExpense(expense);

                Toast.makeText(this, "Pengeluaran berhasil dicatat!", Toast.LENGTH_SHORT).show();
                etFormKeterangan.setText("");
                etFormNominal.setText("");
                loadDashboardSummary();

            } catch (Exception e) {
                Toast.makeText(this, "Gagal mencatat pengeluaran", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bukaMenuRiwayat() {
        tvJudulKonten.setText("Riwayat Aktivitas & Ringkasan");
        btnAksiTambah.setVisibility(View.GONE);
        rvKontenList.setVisibility(View.VISIBLE);
        layoutFormInput.setVisibility(View.GONE);

        try {
            List<Sale> sales = db.saleDao().getAllSales();
            List<Expense> expenses = db.expenseDao().getAllExpenses();
            List<Product> products = db.productDao().getAllProducts();

            List<String> historyList = new ArrayList<>();
            historyList.add("=== REKAPITULASI USAHA ===");
            historyList.add("Total Jenis Produk: " + products.size() + " Item");

            double totalPenjualan = 0;
            double totalKeuntunganKotor = 0;
            for (Sale s : sales) {
                totalPenjualan += s.total_harga;
                totalKeuntunganKotor += s.keuntungan;
            }
            double totalPengeluaran = 0;
            for (Expense e : expenses) {
                totalPengeluaran += e.nominal;
            }
            double keuntunganBersih = totalKeuntunganKotor - totalPengeluaran;

            historyList.add("Total Pendapatan: " + CurrencyFormat.formatRupiah(totalPenjualan));
            historyList.add("Total Keuntungan Kotor: " + CurrencyFormat.formatRupiah(totalKeuntunganKotor));
            historyList.add("Total Pengeluaran: " + CurrencyFormat.formatRupiah(totalPengeluaran));
            historyList.add("Keuntungan Bersih: " + CurrencyFormat.formatRupiah(keuntunganBersih));

            historyList.add("\n=== DAFTAR PRODUK ===");
            if (products.isEmpty()) {
                historyList.add("(Belum ada produk)");
            } else {
                for (Product p : products) {
                    historyList.add("• " + p.nama_produk + " | Stok: " + p.stok + " | Jual: " + CurrencyFormat.formatRupiah(p.harga_jual));
                }
            }

            historyList.add("\n=== CATATAN PENJUALAN ===");
            if (sales.isEmpty()) {
                historyList.add("(Belum ada transaksi penjualan)");
            } else {
                int no = 1;
                for (Sale s : sales) {
                    historyList.add(no++ + ". Jual: " + CurrencyFormat.formatRupiah(s.total_harga) + " | Untung: " + CurrencyFormat.formatRupiah(s.keuntungan));
                }
            }

            historyList.add("\n=== CATATAN PENGELUARAN ===");
            if (expenses.isEmpty()) {
                historyList.add("(Belum ada catatan pengeluaran)");
            } else {
                int no = 1;
                for (Expense e : expenses) {
                    historyList.add(no++ + ". " + e.keterangan + " : " + CurrencyFormat.formatRupiah(e.nominal));
                }
            }

            if (historyAdapter == null) {
                historyAdapter = new HistoryAdapter(historyList);
            } else {
                historyAdapter.setHistoryList(historyList);
            }
            rvKontenList.setAdapter(historyAdapter);

        } catch (Exception e) {
            Toast.makeText(this, "Gagal memuat riwayat", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProductSpinner() {
        productList = db.productDao().getAllProducts();
        List<String> productNames = new ArrayList<>();
        for (Product p : productList) {
            productNames.add(p.nama_produk + " (Stok: " + p.stok + " - Rp " + p.harga_jual + ")");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, productNames);
        spinnerProduk.setAdapter(adapter);
    }

    private void loadProductsData() {
        productList = db.productDao().getAllProducts();
        productAdapter.setProductList(productList);
    }

    private void showAddProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_product, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();

        EditText etNamaProduk = view.findViewById(R.id.etNamaProduk);
        EditText etHargaModal = view.findViewById(R.id.etHargaModal);
        EditText etHargaJual = view.findViewById(R.id.etHargaJual);
        EditText etStok = view.findViewById(R.id.etStok);
        Button btnPilihFoto = view.findViewById(R.id.btnPilihFoto);
        Button btnSimpanProduk = view.findViewById(R.id.btnSimpanProduk);
        ivDialogPreview = view.findViewById(R.id.ivDialogPreview);

        selectedImageUri = "";

        btnPilihFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        btnSimpanProduk.setOnClickListener(v -> {
            String nama = etNamaProduk.getText().toString().trim();
            String modalStr = etHargaModal.getText().toString().trim();
            String jualStr = etHargaJual.getText().toString().trim();
            String stokStr = etStok.getText().toString().trim();

            if (nama.isEmpty() || modalStr.isEmpty() || jualStr.isEmpty() || stokStr.isEmpty()) {
                Toast.makeText(this, "Semua data wajib diisi!", Toast.LENGTH_SHORT).show();
            } else {
                Product product = new Product();
                product.nama_produk = nama;
                product.harga_modal = Double.parseDouble(modalStr);
                product.harga_jual = Double.parseDouble(jualStr);
                product.stok = Integer.parseInt(stokStr);
                product.foto_uri = selectedImageUri;

                db.productDao().insertProduct(product);
                Toast.makeText(this, "Produk berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                loadProductsData();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardSummary();
        loadProductsData();
    }

    private void loadDashboardSummary() {
        try {
            List<Sale> sales = db.saleDao().getAllSales();
            List<Expense> expenses = db.expenseDao().getAllExpenses();
            double totalPenjualan = 0;
            double totalKeuntunganKotor = 0;
            double totalPengeluaran = 0;

            for (Sale sale : sales) {
                totalPenjualan += sale.total_harga;
                totalKeuntunganKotor += sale.keuntungan;
            }

            for (Expense expense : expenses) {
                totalPengeluaran += expense.nominal;
            }

            double keuntunganBersih = totalKeuntunganKotor - totalPengeluaran;

            tvTotalPenjualan.setText(CurrencyFormat.formatRupiah(totalPenjualan));
            tvTotalKeuntungan.setText(CurrencyFormat.formatRupiah(keuntunganBersih));
        } catch (Exception e) {
            tvTotalPenjualan.setText(CurrencyFormat.formatRupiah(0));
            tvTotalKeuntungan.setText(CurrencyFormat.formatRupiah(0));
        }
    }
}
