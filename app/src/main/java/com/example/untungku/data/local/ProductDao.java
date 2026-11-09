package com.example.untungku.data.local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.untungku.data.entity.Product;
import java.util.List;

@Dao
public interface ProductDao {
    @Insert
    void insertProduct(Product product);

    @Update
    void updateProduct(Product product);

    @Delete
    void deleteProduct(Product product);

    @Query("SELECT * FROM products")
    List<Product> getAllProducts();

    @Query("UPDATE products SET stok = stok - :jumlah WHERE id_produk = :idProduk")
    void updateStock(int idProduk, int jumlah);
}