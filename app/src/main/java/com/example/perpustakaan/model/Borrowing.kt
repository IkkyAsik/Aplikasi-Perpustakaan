package com.example.perpustakaan.model

data class Borrowing(
    val id: Int = 0,
    val userId: Int,
    val bookId: Int,
    val bookTitle: String,
    val bookAuthor: String,
    val coverColor: String = "#2980B9",
    val coverImage: String = "",
    val borrowDate: String = "",
    val returnDate: String = "",
    val status: String = "borrowed",
    val actualReturnDate: String? = null
)
