package com.example.perpustakaan

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.perpustakaan.database.DatabaseHelper
import com.example.perpustakaan.databinding.ActivityBookDetailBinding
import com.example.perpustakaan.model.Borrowing
import com.example.perpustakaan.utils.ImageUtils

class BookDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookDetailBinding
    private lateinit var db: DatabaseHelper
    private var bookId: Int = -1
    private var userId: Int = -1
    private var activeBorrowing: Borrowing? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)
        userId = getSharedPreferences("PerpustakaanApp", MODE_PRIVATE).getInt("user_id", -1)
        bookId = intent.getIntExtra("book_id", -1)

        if (bookId == -1) { finish(); return }

        binding.btnBack.setOnClickListener { finish() }
        loadBookDetail()
    }

    override fun onResume() {
        super.onResume()
        if (bookId != -1) loadBookDetail()
    }

    private fun loadBookDetail() {
        val book = db.getBookById(bookId) ?: run { finish(); return }

        ImageUtils.loadBookCover(this, book.coverImage, book.coverColor, binding.ivCoverImage)
        
        try {
            binding.layoutHeader.setBackgroundColor(Color.parseColor(book.coverColor))
        } catch (e: Exception) {
            binding.layoutHeader.setBackgroundColor(Color.parseColor("#2980B9"))
        }

        binding.tvBookTitle.text = book.title
        binding.tvBookAuthor.text = book.author
        binding.tvCategory.text = book.category
        binding.tvYear.text = book.year
        binding.tvIsbn.text = if (book.isbn.isNotEmpty()) book.isbn else "-"
        binding.tvAvailableCopies.text =
            "${book.availableCopies} ${getString(R.string.copies_of)} ${book.totalCopies} ${getString(R.string.copies)}"

        if (book.availableCopies > 0) {
            binding.tvAvailableCopies.setTextColor(Color.parseColor("#27AE60"))
        } else {
            binding.tvAvailableCopies.setTextColor(Color.parseColor("#E74C3C"))
        }

        binding.tvDescription.text = book.description.ifEmpty { "Tidak ada deskripsi." }

        activeBorrowing = if (userId > 0) db.getActiveBorrowingForBook(userId, bookId) else null
        updateButton(book.availableCopies)
    }

    private fun updateButton(availableCopies: Int) {
        when {
            activeBorrowing != null -> {
                binding.btnBorrowReturn.text = getString(R.string.btn_return)
                binding.btnBorrowReturn.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#E67E22"))
                binding.btnBorrowReturn.isEnabled = true
                binding.btnBorrowReturn.setOnClickListener { returnBook() }
            }
            availableCopies > 0 -> {
                binding.btnBorrowReturn.text = getString(R.string.btn_borrow)
                binding.btnBorrowReturn.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#1B4F72"))
                binding.btnBorrowReturn.isEnabled = true
                binding.btnBorrowReturn.setOnClickListener { borrowBook() }
            }
            else -> {
                binding.btnBorrowReturn.text = getString(R.string.btn_unavailable)
                binding.btnBorrowReturn.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#AAAAAA"))
                binding.btnBorrowReturn.isEnabled = false
            }
        }
    }

    private fun borrowBook() {
        if (userId <= 0) {
            Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }
        if (db.borrowBook(userId, bookId)) {
            Toast.makeText(this, getString(R.string.borrow_success), Toast.LENGTH_SHORT).show()
            loadBookDetail()
        } else {
            Toast.makeText(this, "Buku tidak tersedia saat ini", Toast.LENGTH_SHORT).show()
        }
    }

    private fun returnBook() {
        val borrowing = activeBorrowing ?: return
        if (db.returnBook(borrowing.id, bookId)) {
            Toast.makeText(this, getString(R.string.return_success), Toast.LENGTH_SHORT).show()
            activeBorrowing = null
            loadBookDetail()
        } else {
            Toast.makeText(this, "Gagal mengembalikan buku", Toast.LENGTH_SHORT).show()
        }
    }
}
