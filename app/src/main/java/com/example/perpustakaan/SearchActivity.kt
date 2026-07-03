package com.example.perpustakaan

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.perpustakaan.adapter.BookAdapter
import com.example.perpustakaan.database.DatabaseHelper
import com.example.perpustakaan.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var db: DatabaseHelper
    private lateinit var bookAdapter: BookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        binding.btnBack.setOnClickListener { finish() }

        bookAdapter = BookAdapter { book ->
            val intent = Intent(this, BookDetailActivity::class.java)
            intent.putExtra("book_id", book.id)
            startActivity(intent)
        }
        binding.rvSearchResults.adapter = bookAdapter

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.isEmpty()) {
                    showEmpty()
                } else {
                    searchBooks(query)
                }
            }
        })
    }

    private fun searchBooks(query: String) {
        val results = db.searchBooks(query)
        bookAdapter.submitList(results)
        if (results.isEmpty()) {
            showEmpty()
        } else {
            binding.layoutEmpty.visibility = View.GONE
            binding.rvSearchResults.visibility = View.VISIBLE
            binding.tvResultsLabel.visibility = View.VISIBLE
            binding.tvResultsLabel.text = "${results.size} hasil ditemukan"
        }
    }

    private fun showEmpty() {
        binding.layoutEmpty.visibility = View.VISIBLE
        binding.rvSearchResults.visibility = View.GONE
        binding.tvResultsLabel.visibility = View.GONE
    }
}
