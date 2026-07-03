package com.example.perpustakaan

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.perpustakaan.adapter.ReviewAdapter
import com.example.perpustakaan.database.DatabaseHelper
import com.example.perpustakaan.databinding.ActivityBookDetailBinding
import com.example.perpustakaan.model.Borrowing
import com.example.perpustakaan.model.Review
import com.example.perpustakaan.utils.ImageUtils

class BookDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookDetailBinding
    private lateinit var db: DatabaseHelper
    private var bookId: Int = -1
    private var userId: Int = -1
    private var activeBorrowing: Borrowing? = null
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)
        userId = getSharedPreferences("PerpustakaanApp", MODE_PRIVATE).getInt("user_id", -1)
        bookId = intent.getIntExtra("book_id", -1)

        if (bookId == -1) { finish(); return }

        binding.btnBack.setOnClickListener { finish() }
        binding.rvReviews.layoutManager = LinearLayoutManager(this)

        setupFavorite()
        setupReviewButton()
        loadBookDetail()
        loadReviews()
    }

    // FUNGSI INI DITAMBAHKAN UNTUK MENGATASI ERROR isAdmin
    private fun checkIfUserIsAdmin(): Boolean {
        val cursor = db.readableDatabase.rawQuery("SELECT role FROM users WHERE id = ?", arrayOf(userId.toString()))
        var isAdmin = false
        if (cursor.moveToFirst()) {
            val role = cursor.getString(0)
            isAdmin = (role == "admin")
        }
        cursor.close()
        return isAdmin
    }

    override fun onResume() {
        super.onResume()
        if (bookId != -1) {
            loadBookDetail()
            loadReviews()
        }
    }

    private fun setupFavorite() {
        if (userId > 0) {
            isFavorite = db.isFavorite(userId, bookId)
            updateFavoriteIcon()
            binding.btnFavorite.setOnClickListener {
                if (isFavorite) {
                    db.removeFavorite(userId, bookId)
                    isFavorite = false
                    Toast.makeText(this, "Dihapus dari favorit", Toast.LENGTH_SHORT).show()
                } else {
                    db.addFavorite(userId, bookId)
                    isFavorite = true
                    Toast.makeText(this, "Ditambahkan ke favorit ❤", Toast.LENGTH_SHORT).show()
                }
                updateFavoriteIcon()
            }
        } else {
            binding.btnFavorite.visibility = View.GONE
        }
    }

    private fun updateFavoriteIcon() {
        binding.btnFavorite.setImageResource(
            if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        )
    }

    private fun setupReviewButton() {
        binding.btnAddReview.setOnClickListener {
            if (userId <= 0) {
                Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (db.hasUserReviewed(userId, bookId)) {
                Toast.makeText(this, "Anda sudah memberikan ulasan untuk buku ini", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showAddReviewDialog()
        }
    }

    private fun showAddReviewDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_review, null)
        val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)
        val etComment = dialogView.findViewById<EditText>(R.id.etComment)

        AlertDialog.Builder(this)
            .setTitle("Tulis Ulasan")
            .setView(dialogView)
            .setPositiveButton("Kirim") { _, _ ->
                val rating = ratingBar.rating
                if (rating == 0f) {
                    Toast.makeText(this, "Silakan beri rating bintang", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val review = Review(userId = userId, bookId = bookId, rating = rating, comment = etComment.text.toString().trim())
                if (db.addReview(review)) {
                    Toast.makeText(this, "Ulasan berhasil ditambahkan ⭐", Toast.LENGTH_SHORT).show()
                    loadReviews()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun loadReviews() {
        val reviews = db.getReviewsForBook(bookId)
        if (reviews.isNotEmpty()) {
            val avg = db.getAverageRating(bookId)
            binding.layoutRating.visibility = View.VISIBLE
            binding.tvAverageRating.text = String.format("%.1f", avg)
            binding.tvReviewCount.text = "(${reviews.size} ulasan)"
            binding.tvNoReviews.visibility = View.GONE
            binding.rvReviews.visibility = View.VISIBLE

            val adapter = ReviewAdapter(
                onEdit = { review -> showEditReviewDialog(review) },
                onDelete = { review -> confirmDeleteReview(review) },
                // MENGGUNAKAN FUNGSI LOKAL checkIfUserIsAdmin()
                canModify = { review -> review.userId == userId || checkIfUserIsAdmin() }
            )
            binding.rvReviews.adapter = adapter
            adapter.submitList(reviews)
        } else {
            binding.layoutRating.visibility = View.GONE
            binding.tvNoReviews.visibility = View.VISIBLE
            binding.rvReviews.visibility = View.GONE
        }
    }

    private fun showEditReviewDialog(review: Review) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_review, null)
        val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)
        val etComment = dialogView.findViewById<EditText>(R.id.etComment)
        ratingBar.rating = review.rating
        etComment.setText(review.comment)
        AlertDialog.Builder(this)
            .setTitle("Edit Review")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val updatedReview = review.copy(rating = ratingBar.rating, comment = etComment.text.toString().trim())
                if (db.updateReview(updatedReview)) {
                    Toast.makeText(this, "Ulasan berhasil diubah", Toast.LENGTH_SHORT).show()
                    loadReviews()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun confirmDeleteReview(review: Review) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Ulasan")
            .setMessage("Apakah Anda yakin ingin menghapus ulasan ini?")
            .setPositiveButton("Ya") { _, _ ->
                if (db.deleteReview(review.id, review.userId)) {
                    Toast.makeText(this, "Ulasan dihapus", Toast.LENGTH_SHORT).show()
                    loadReviews()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun loadBookDetail() {
        val book = db.getBookById(bookId) ?: run { finish(); return }
        ImageUtils.loadBookCover(this, book.coverImage, book.coverColor, book.title, book.author, binding.ivCoverImage)
        try { binding.layoutHeader.setBackgroundColor(Color.parseColor(book.coverColor)) }
        catch (e: Exception) { binding.layoutHeader.setBackgroundColor(Color.parseColor("#2980B9")) }
        binding.tvBookTitle.text = book.title
        binding.tvBookAuthor.text = book.author
        binding.tvAvailableCopies.text = "${book.availableCopies} copy tersedia"
        binding.tvDescription.text = book.description.ifEmpty { "Tidak ada deskripsi." }
        activeBorrowing = if (userId > 0) db.getActiveBorrowingForBook(userId, bookId) else null
        updateButton(book.availableCopies)
    }

    private fun updateButton(availableCopies: Int) {
        when {
            activeBorrowing != null -> {
                binding.btnBorrowReturn.text = "Kembalikan"
                binding.btnBorrowReturn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E67E22"))
                binding.btnBorrowReturn.setOnClickListener { returnBook() }
            }
            availableCopies > 0 -> {
                binding.btnBorrowReturn.text = "Pinjam"
                binding.btnBorrowReturn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1B4F72"))
                binding.btnBorrowReturn.setOnClickListener { borrowBook() }
            }
            else -> {
                binding.btnBorrowReturn.text = "Tidak Tersedia"
                binding.btnBorrowReturn.isEnabled = false
            }
        }
    }

    private fun borrowBook() {
        if (userId <= 0) { Toast.makeText(this, "Login dulu", Toast.LENGTH_SHORT).show(); return }
        if (db.borrowBook(userId, bookId)) { loadBookDetail() }
    }

    private fun returnBook() {
        val borrowing = activeBorrowing ?: return
        if (db.returnBook(borrowing.id, bookId)) { loadBookDetail() }
    }
}