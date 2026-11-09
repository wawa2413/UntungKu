package com.example.untungku.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "products")
public class Product {
    @PrimaryKey(autoGenerate = true)
    public int id_produk;

    public String nama_produk;
    public double harga_modal;
    public double harga_jual;
    public int stok;
    public String foto_uri;
}