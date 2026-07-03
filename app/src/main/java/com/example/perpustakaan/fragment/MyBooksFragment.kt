package com.example.perpustakaan.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.perpustakaan.adapter.BorrowingAdapter
import com.example.perpustakaan.database.DatabaseHelper
import com.example.perpustakaan.databinding.FragmentMyBooksBinding
import com.example.perpustakaan.model.Borrowing

class MyBooksFragment : Fragment() {

    private var _binding: FragmentMyBooksBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: DatabaseHelper
    private lateinit var adapter: BorrowingAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyBooksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(requireContext())

        adapter = BorrowingAdapter { borrowing ->
            if (borrowing.status == "borrowed") showReturnDialog(borrowing)
        }
        binding.rvBorrowings.adapter = adapter
        loadBorrowings()
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) loadBorrowings()
    }

    private fun loadBorrowings() {
        val userId = requireContext()
            .getSharedPreferences("PerpustakaanApp", Context.MODE_PRIVATE)
            .getInt("user_id", 0)

        if (userId <= 0) { showEmpty(); return }

        val list = db.getUserBorrowings(userId)
        adapter.submitList(list)
        if (list.isEmpty()) showEmpty()
        else {
            binding.layoutEmpty.visibility = View.GONE
            binding.rvBorrowings.visibility = View.VISIBLE
        }
    }

    private fun showEmpty() {
        binding.layoutEmpty.visibility = View.VISIBLE
        binding.rvBorrowings.visibility = View.GONE
    }

    private fun showReturnDialog(borrowing: Borrowing) {
        AlertDialog.Builder(requireContext())
            .setTitle("Kembalikan Buku")
            .setMessage("Kembalikan \"${borrowing.bookTitle}\"?")
            .setPositiveButton("Ya, Kembalikan") { _, _ ->
                if (db.returnBook(borrowing.id, borrowing.bookId)) {
                    Toast.makeText(requireContext(), "Buku berhasil dikembalikan!", Toast.LENGTH_SHORT).show()
                    loadBorrowings()
                } else {
                    Toast.makeText(requireContext(), "Gagal mengembalikan buku", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
