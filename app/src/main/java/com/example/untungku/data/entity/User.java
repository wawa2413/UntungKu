package com.example.untungku.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id_user;
    public String nama;
    public String email;
    public String password;
}