package com.example.perpustakaan.model

data class Book(
    val id: Int = 0,
    val title: String,
    val author: String,
    val category: String,
    val description: String = "",
    val year: String = "",
    val totalCopies: Int,
    val availableCopies: Int,
    val isbn: String,
    val coverColor: String,
    val coverImage: String = "" // Can be URL, local drawable name, or empty
)
