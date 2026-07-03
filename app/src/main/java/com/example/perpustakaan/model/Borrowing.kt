package com.example.perpustakaan.model

data class Borrowing(
    val id: Int,
    val userId: Int,
    val bookId: Int,
    val bookTitle: String,
    val bookAuthor: String,
    val coverColor: String,
    val coverImage: String,
    val borrowDate: String,
    val returnDate: String,
    val dueDate: String,
    val status: String,
    val notified: Boolean,
    val actualReturnDate: String?
) {
    fun isOverdue(): Boolean {
        if (status != "borrowed") return false
        return try {
            val fmt = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale("id", "ID"))
            val due = fmt.parse(dueDate)
            due != null && java.util.Date().after(due)
        } catch (e: Exception) {
            false
        }
    }
}