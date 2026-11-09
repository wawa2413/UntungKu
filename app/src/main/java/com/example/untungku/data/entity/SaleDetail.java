package com.example.untungku.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sale_details")
public class SaleDetail {
    @PrimaryKey(autoGenerate = true)
    public int id_detail;
    public int id_penjualan;
    public int id_produk;
    public int jumlah;
    public double harga_modal;
    public double harga_jual;
    public double subtotal;
}