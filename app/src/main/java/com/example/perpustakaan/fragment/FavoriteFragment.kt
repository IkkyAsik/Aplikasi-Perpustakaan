package com.example.perpustakaan.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.perpustakaan.BookDetailActivity
import com.example.perpustakaan.adapter.BookAdapter
import com.example.perpustakaan.database.DatabaseHelper
import com.example.perpustakaan.databinding.FragmentFavoriteBinding

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: DatabaseHelper
    private var userId: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(requireContext())
        userId = requireContext().getSharedPreferences("PerpustakaanApp", Context.MODE_PRIVATE).getInt("user_id", -1)
        binding.rvFavorites.layoutManager = LinearLayoutManager(requireContext())
        loadFavorites()
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) loadFavorites()
    }

    private fun loadFavorites() {
        val favorites = if (userId > 0) db.getUserFavorites(userId) else emptyList()
        if (favorites.isEmpty()) {
            binding.layoutEmpty.visibility = View.VISIBLE
            binding.rvFavorites.visibility = View.GONE
        } else {
            binding.layoutEmpty.visibility = View.GONE
            binding.rvFavorites.visibility = View.VISIBLE
            val adapter = BookAdapter { book ->
                startActivity(Intent(requireContext(), BookDetailActivity::class.java).apply {
                    putExtra("book_id", book.id)
                })
            }
            binding.rvFavorites.adapter = adapter
            adapter.submitList(favorites)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
