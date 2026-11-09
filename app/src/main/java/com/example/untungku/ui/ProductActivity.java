package com.example.untungku.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.untungku.R;
import com.example.untungku.data.entity.Product;
import com.example.untungku.data.local.AppDatabase;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {

    private RecyclerView rvProducts;
    private Button btnTambahProduk;
    private ProductAdapter adapter;
    private AppDatabase db;
    private List<Product> productList = new ArrayList<>();

    private String selectedImageUri = "";
    private ImageView ivDialogPreview;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        db = AppDatabase.getDatabase(this);

        rvProducts = findViewById(R.id.rvProducts);
        btnTambahProduk = findViewById(R.id.btnTambahProduk);

        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(productList);
        rvProducts.setAdapter(adapter);

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
                            Toast.makeText(this, "Foto berhasil dipilih!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        loadProducts();
        btnTambahProduk.setOnClickListener(v -> showAddProductDialog());
    }

    private void loadProducts() {
        productList = db.productDao().getAllProducts();
        adapter.setProductList(productList);
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
                Toast.makeText(ProductActivity.this, "Semua data wajib diisi!", Toast.LENGTH_SHORT).show();
            } else {
                Product product = new Product();
                product.nama_produk = nama;
                product.harga_modal = Double.parseDouble(modalStr);
                product.harga_jual = Double.parseDouble(jualStr);
                product.stok = Integer.parseInt(stokStr);
                product.foto_uri = selectedImageUri;

                db.productDao().insertProduct(product);
                Toast.makeText(ProductActivity.this, "Produk berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                loadProducts();
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}