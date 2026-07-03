package com.example.perpustakaan

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.perpustakaan.adapter.AdminBookAdapter
import com.example.perpustakaan.database.DatabaseHelper
import com.example.perpustakaan.databinding.ActivityAdminDashboardBinding
import com.example.perpustakaan.model.Book

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding
    private lateinit var db: DatabaseHelper
    private lateinit var adapter: AdminBookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        binding.btnBack.setOnClickListener { finish() }

        binding.fabAddBook.setOnClickListener {
            startActivity(Intent(this, AddEditBookActivity::class.java))
        }

        adapter = AdminBookAdapter(
            onEditClick = { book ->
                val intent = Intent(this, AddEditBookActivity::class.java)
                intent.putExtra("book_id", book.id)
                startActivity(intent)
            },
            onDeleteClick = { book ->
                showDeleteDialog(book)
            }
        )
        binding.rvAdminBooks.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadBooks()
    }

    private fun loadBooks() {
        val books = db.getAllBooks()
        adapter.submitList(books)
    }

    private fun showDeleteDialog(book: Book) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Buku")
            .setMessage("Yakin ingin menghapus '${book.title}'?")
            .setPositiveButton("Hapus") { _, _ ->
                if (db.deleteBook(book.id) > 0) {
                    Toast.makeText(this, "Buku berhasil dihapus", Toast.LENGTH_SHORT).show()
                    loadBooks()
                } else {
                    Toast.makeText(this, "Gagal menghapus buku", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
