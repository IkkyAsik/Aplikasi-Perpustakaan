package com.example.perpustakaan.model

data class User(
    val id: Int = 0,
    val name: String,
    val email: String,
    val password: String,
    val phone: String = "",
    val createdAt: String = ""
)

data class MemberBorrowInfo(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val createdAt: String,
    val activeBorrowCount: Int
)
