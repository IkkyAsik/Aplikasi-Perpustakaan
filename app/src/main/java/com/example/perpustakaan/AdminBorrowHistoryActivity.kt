package com.example.perpustakaan

import android.content.Context
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.perpustakaan.adapter.BorrowingAdapter
import com.example.perpustakaan.database.DatabaseHelper
import com.example.perpustakaan.databinding.ActivityAdminBorrowHistoryBinding

class AdminBorrowHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBorrowHistoryBinding
    private lateinit var db: DatabaseHelper
    private lateinit var adapter: BorrowingAdapter
    private var showOnlyActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBorrowHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)
        adapter = BorrowingAdapter(onReturnClick = {}) // Admin view - no return action
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = adapter

        binding.btnBack.setOnClickListener { finish() }

        binding.chipAll.isChecked = true
        binding.chipAll.setOnClickListener {
            showOnlyActive = false
            loadHistory()
        }
        binding.chipActive.setOnClickListener {
            showOnlyActive = true
            loadHistory()
        }

        loadHistory()
    }

    private fun loadHistory() {
        val all = db.getAllBorrowings()
        val filtered = if (showOnlyActive) all.filter { it.status == "borrowed" } else all
        adapter.submitList(filtered)
    }
}
