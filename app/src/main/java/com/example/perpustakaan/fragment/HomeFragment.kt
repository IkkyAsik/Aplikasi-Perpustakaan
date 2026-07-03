package com.example.perpustakaan.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.perpustakaan.BookDetailActivity
import com.example.perpustakaan.HomeActivity
import com.example.perpustakaan.R
import com.example.perpustakaan.adapter.BookAdapter
import com.example.perpustakaan.database.DatabaseHelper
import com.example.perpustakaan.databinding.FragmentHomeBinding
import java.util.Calendar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: DatabaseHelper
    private lateinit var bookAdapter: BookAdapter
    private var selectedCategory = "Semua"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(requireContext())

        setupGreeting()
        setupBookList()
        setupCategories()

        binding.tvSearchHint.setOnClickListener {
            (activity as? HomeActivity)?.switchToSearch()
        }
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) loadBooks(selectedCategory)
    }

    private fun setupGreeting() {
        val prefs = requireContext().getSharedPreferences("PerpustakaanApp", Context.MODE_PRIVATE)
        val userName = prefs.getString("user_name", "User") ?: "User"
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when {
            hour < 12 -> getString(R.string.greeting_morning)
            hour < 17 -> getString(R.string.greeting_afternoon)
            else -> getString(R.string.greeting_evening)
        }
        binding.tvGreeting.text = greeting
        binding.tvUsername.text = userName
    }

    private fun setupBookList() {
        bookAdapter = BookAdapter { book ->
            val intent = Intent(requireContext(), BookDetailActivity::class.java)
            intent.putExtra("book_id", book.id)
            startActivity(intent)
        }
        binding.rvBooks.adapter = bookAdapter
        loadBooks("Semua")
    }

    private fun setupCategories() {
        val categories = listOf("Semua", "Novel", "Sastra", "Teknologi", "Sains", "Sejarah", "Filsafat", "Bisnis", "Pendidikan")
        binding.chipGroup.removeAllViews()
        categories.forEach { cat ->
            val chip = com.google.android.material.chip.Chip(requireContext()).apply {
                text = cat
                isCheckable = true
                isChecked = cat == selectedCategory
                setOnClickListener {
                    selectedCategory = cat
                    loadBooks(cat)
                    updateChips(categories, cat)
                }
            }
            binding.chipGroup.addView(chip)
        }
    }

    private fun updateChips(categories: List<String>, selected: String) {
        for (i in 0 until binding.chipGroup.childCount) {
            val chip = binding.chipGroup.getChildAt(i) as? com.google.android.material.chip.Chip
            chip?.isChecked = chip?.text == selected
        }
    }

    private fun loadBooks(category: String) {
        val books = if (category == "Semua") db.getAllBooks() else db.getBooksByCategory(category)
        bookAdapter.submitList(books)
        binding.tvNoBooksMessage.visibility = if (books.isEmpty()) View.VISIBLE else View.GONE
        binding.rvBooks.visibility = if (books.isEmpty()) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
