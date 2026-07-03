package com.example.perpustakaan.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.perpustakaan.R
import com.example.perpustakaan.adapter.MemberAdapter
import com.example.perpustakaan.adapter.MemberBorrowDetailAdapter
import com.example.perpustakaan.database.DatabaseHelper
import com.example.perpustakaan.databinding.FragmentMembersBinding
import com.example.perpustakaan.model.MemberBorrowInfo

class MembersFragment : Fragment() {

    private var _binding: FragmentMembersBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: DatabaseHelper
    private lateinit var adapter: MemberAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMembersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(requireContext())

        setupRecyclerView()
        setupSearch()
        loadMembers()
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) {
            loadMembers()
        }
    }

    private fun setupRecyclerView() {
        adapter = MemberAdapter { member ->
            showBorrowingsDialog(member)
        }
        binding.rvMembers.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearchMember.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
                updateEmptyState()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadMembers() {
        val members = db.getAllMembersWithActiveBorrowCount()
        adapter.submitList(members)
        updateEmptyState()
    }

    private fun updateEmptyState() {
        if (_binding == null) return
        val count = adapter.itemCount
        binding.tvNoMembers.visibility = if (count == 0) View.VISIBLE else View.GONE
        binding.rvMembers.visibility = if (count == 0) View.GONE else View.VISIBLE
    }

    private fun showBorrowingsDialog(member: MemberBorrowInfo) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_member_borrowings, null)
        val tvMemberNameTitle = dialogView.findViewById<TextView>(R.id.tvMemberNameTitle)
        val rvMemberBorrowings = dialogView.findViewById<RecyclerView>(R.id.rvMemberBorrowings)
        val tvNoBorrowings = dialogView.findViewById<TextView>(R.id.tvNoBorrowings)
        val btnClose = dialogView.findViewById<Button>(R.id.btnClose)

        tvMemberNameTitle.text = member.name
        rvMemberBorrowings.layoutManager = LinearLayoutManager(requireContext())

        val activeBorrowings = db.getUserBorrowings(member.id).filter { it.status == "borrowed" }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        if (activeBorrowings.isNotEmpty()) {
            val detailAdapter = MemberBorrowDetailAdapter()
            rvMemberBorrowings.adapter = detailAdapter
            detailAdapter.submitList(activeBorrowings)
            tvNoBorrowings.visibility = View.GONE
            rvMemberBorrowings.visibility = View.VISIBLE
        } else {
            tvNoBorrowings.visibility = View.VISIBLE
            rvMemberBorrowings.visibility = View.GONE
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
