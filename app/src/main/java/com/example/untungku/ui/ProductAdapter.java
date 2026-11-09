package com.example.untungku.ui;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.untungku.CurrencyFormat;
import com.example.untungku.R;
import com.example.untungku.data.entity.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvNamaProduk.setText(product.nama_produk);
        holder.tvHargaModal.setText("Modal: " + CurrencyFormat.formatRupiah(product.harga_modal));
        holder.tvHargaJual.setText("Jual: " + CurrencyFormat.formatRupiah(product.harga_jual));
        holder.tvStok.setText("Stok: " + product.stok);

        if (product.foto_uri != null && !product.foto_uri.isEmpty()) {
            try {
                holder.ivProductPhoto.setImageURI(Uri.parse(product.foto_uri));
            } catch (Exception e) {
                holder.ivProductPhoto.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            holder.ivProductPhoto.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamaProduk, tvHargaModal, tvHargaJual, tvStok;
        ImageView ivProductPhoto;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaProduk = itemView.findViewById(R.id.tvNamaProduk);
            tvHargaModal = itemView.findViewById(R.id.tvHargaModal);
            tvHargaJual = itemView.findViewById(R.id.tvHargaJual);
            tvStok = itemView.findViewById(R.id.tvStok);
            ivProductPhoto = itemView.findViewById(R.id.ivProductPhoto);
        }
    }
}