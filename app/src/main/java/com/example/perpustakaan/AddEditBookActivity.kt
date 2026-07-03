package com.example.perpustakaan

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.perpustakaan.database.DatabaseHelper
import com.example.perpustakaan.databinding.ActivityAddEditBookBinding
import com.example.perpustakaan.model.Book

class AddEditBookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditBookBinding
    private lateinit var db: DatabaseHelper
    private var bookId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        binding.btnBack.setOnClickListener { finish() }

        bookId = intent.getIntExtra("book_id", -1)
        if (bookId != -1) {
            binding.tvToolbarTitle.text = "Edit Buku"
            loadBookData()
        } else {
            binding.tvToolbarTitle.text = "Tambah Buku"
        }

        binding.btnSave.setOnClickListener {
            saveBook()
        }
    }

    private fun loadBookData() {
        val book = db.getBookById(bookId)
        if (book != null) {
            binding.etTitle.setText(book.title)
            binding.etAuthor.setText(book.author)
            binding.etCategory.setText(book.category)
            binding.etDescription.setText(book.description)
            binding.etYear.setText(book.year)
            binding.etTotalCopies.setText(book.totalCopies.toString())
            binding.etIsbn.setText(book.isbn)
            binding.etCoverColor.setText(book.coverColor)
            binding.etCoverImage.setText(book.coverImage)
        }
    }

    private fun saveBook() {
        val title = binding.etTitle.text.toString().trim()
        val author = binding.etAuthor.text.toString().trim()
        val category = binding.etCategory.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val year = binding.etYear.text.toString().trim()
        val totalCopiesStr = binding.etTotalCopies.text.toString().trim()
        val isbn = binding.etIsbn.text.toString().trim()
        val coverColor = binding.etCoverColor.text.toString().trim().ifEmpty { "#2980B9" }
        val coverImage = binding.etCoverImage.text.toString().trim()

        if (title.isEmpty() || author.isEmpty() || category.isEmpty() || totalCopiesStr.isEmpty()) {
            Toast.makeText(this, "Judul, Pengarang, Kategori, dan Total Eksemplar wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val totalCopies = totalCopiesStr.toIntOrNull() ?: 0

        val book = Book(
            id = if (bookId != -1) bookId else 0,
            title = title,
            author = author,
            category = category,
            description = description,
            year = year,
            totalCopies = totalCopies,
            availableCopies = totalCopies, // Simplified for now, editing total copies resets available
            isbn = isbn,
            coverColor = coverColor,
            coverImage = coverImage
        )

        if (bookId != -1) {
            // Edit existing book
            val oldBook = db.getBookById(bookId)
            if (oldBook != null) {
                // Calculate new available copies based on borrowed amount
                val borrowedCount = oldBook.totalCopies - oldBook.availableCopies
                val newAvailable = if (totalCopies >= borrowedCount) totalCopies - borrowedCount else 0
                val updatedBook = book.copy(availableCopies = newAvailable)
                if (db.updateBook(updatedBook) > 0) {
                    Toast.makeText(this, "Buku berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Gagal memperbarui buku", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // Add new book
            if (db.addBook(book) > 0) {
                Toast.makeText(this, "Buku berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Gagal menambahkan buku", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
