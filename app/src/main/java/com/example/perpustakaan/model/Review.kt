package com.example.perpustakaan.model

data class Review(
    val id: Int = 0,
    val userId: Int,
    val bookId: Int,
    val userName: String = "",
    val rating: Float,
    val comment: String = "",
    val date: String = ""
)
