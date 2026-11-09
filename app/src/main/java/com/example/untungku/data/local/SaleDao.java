package com.example.untungku.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.untungku.data.entity.Sale;
import com.example.untungku.data.entity.SaleDetail;

import java.util.List;

@Dao
public interface SaleDao {

    @Insert
    long insertSale(Sale sale);

    @Insert
    long insertSaleDetail(SaleDetail saleDetail);

    @Query("SELECT SUM(total_penjualan) FROM sales WHERE tanggal = :date")
    Double getTotalSalesToday(String date);

    @Query("SELECT SUM(keuntungan_kotor) FROM sales WHERE tanggal = :date")
    Double getTotalProfitToday(String date);

    @Query("SELECT * FROM sales")
    List<Sale> getAllSales();
}