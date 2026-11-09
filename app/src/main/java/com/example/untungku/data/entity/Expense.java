package com.example.untungku.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "expenses")
public class Expense {
    @PrimaryKey(autoGenerate = true)
    public int id_pengeluaran;
    public String tanggal;
    public String kategori;
    public String keterangan;
    public double nominal;
}