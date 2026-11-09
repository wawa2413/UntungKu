package com.example.untungku.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sales")
public class Sale {
    @PrimaryKey(autoGenerate = true)
    public int id_penjualan;
    public String tanggal; // Format: YYYY-MM-DD
    public double total_penjualan;
    public double total_modal;
    public double keuntungan_kotor;
    public double keuntungan;
    public double total_harga;
}