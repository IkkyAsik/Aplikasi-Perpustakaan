package com.example.perpustakaan.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.perpustakaan.AboutActivity
import com.example.perpustakaan.AdminDashboardActivity
import com.example.perpustakaan.LoginActivity
import com.example.perpustakaan.database.DatabaseHelper
import com.example.perpustakaan.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: DatabaseHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(requireContext())
        loadProfile()

        binding.btnAbout.setOnClickListener {
            startActivity(Intent(requireContext(), AboutActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Keluar")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya") { _, _ -> logout() }
                .setNegativeButton("Tidak", null)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) loadProfile()
    }

    private fun loadProfile() {
        val prefs = requireContext().getSharedPreferences("PerpustakaanApp", Context.MODE_PRIVATE)
        val userId = prefs.getInt("user_id", 0)
        val name = prefs.getString("user_name", "User") ?: "User"
        val email = prefs.getString("user_email", "") ?: ""
        val phone = prefs.getString("user_phone", "") ?: ""
        val memberSince = prefs.getString("user_created_at", "") ?: ""

        binding.tvProfileName.text = name
        binding.tvProfileEmail.text = email
        binding.tvProfilePhone.text = phone.ifEmpty { "-" }
        binding.tvMemberSince.text = memberSince.ifEmpty { "-" }
        binding.tvAvatar.text = if (name.isNotEmpty()) name.first().toString().uppercase() else "U"

        if (userId > 0) {
            binding.tvTotalBorrowed.text = db.getTotalBorrowedCount(userId).toString()
            binding.tvActiveBorrowed.text = db.getActiveBorrowingsCount(userId).toString()
        }

        if (email == "admin@perpustakaan.com") {
            binding.btnAdminManageBooks.visibility = View.VISIBLE
            binding.btnAdminManageBooks.setOnClickListener {
                startActivity(Intent(requireContext(), AdminDashboardActivity::class.java))
            }
        } else {
            binding.btnAdminManageBooks.visibility = View.GONE
        }
    }

    private fun logout() {
        requireContext().getSharedPreferences("PerpustakaanApp", Context.MODE_PRIVATE).edit().clear().apply()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
