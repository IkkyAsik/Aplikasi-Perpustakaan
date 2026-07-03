package com.example.perpustakaan.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.perpustakaan.databinding.ItemMemberBinding
import com.example.perpustakaan.model.MemberBorrowInfo
import java.util.Locale

class MemberAdapter(
    private val onItemClick: (MemberBorrowInfo) -> Unit
) : RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

    private var originalList = listOf<MemberBorrowInfo>()
    private var filteredList = listOf<MemberBorrowInfo>()

    fun submitList(list: List<MemberBorrowInfo>) {
        originalList = list
        filteredList = list
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            originalList
        } else {
            val q = query.lowercase(Locale.getDefault())
            originalList.filter {
                it.name.lowercase(Locale.getDefault()).contains(q) ||
                        it.email.lowercase(Locale.getDefault()).contains(q)
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val binding = ItemMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    override fun getItemCount(): Int = filteredList.size

    inner class MemberViewHolder(private val binding: ItemMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MemberBorrowInfo) {
            binding.tvMemberName.text = item.name
            binding.tvMemberEmail.text = item.email
            binding.tvMemberPhone.text = item.phone.ifEmpty { "-" }
            binding.tvMemberSince.text = "Sejak: ${item.createdAt}"
            binding.tvMemberAvatar.text = if (item.name.isNotEmpty()) item.name.first().toString().uppercase() else "U"

            if (item.activeBorrowCount > 0) {
                binding.tvActiveBorrowBadge.text = "${item.activeBorrowCount} Buku"
                binding.tvActiveBorrowBadge.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E67E22")) // Orange/Warning
            } else {
                binding.tvActiveBorrowBadge.text = "0 Buku"
                binding.tvActiveBorrowBadge.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#27AE60")) // Green/Success
            }

            binding.root.setOnClickListener { onItemClick(item) }
        }
    }
}
