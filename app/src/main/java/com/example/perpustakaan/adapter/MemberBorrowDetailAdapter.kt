package com.example.perpustakaan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.perpustakaan.databinding.ItemMemberBorrowDetailBinding
import com.example.perpustakaan.model.Borrowing

class MemberBorrowDetailAdapter : RecyclerView.Adapter<MemberBorrowDetailAdapter.ViewHolder>() {

    private var list = listOf<Borrowing>()

    fun submitList(newList: List<Borrowing>) {
        list = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMemberBorrowDetailBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(private val binding: ItemMemberBorrowDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Borrowing) {
            binding.tvBookTitle.text = item.bookTitle
            binding.tvBookAuthor.text = item.bookAuthor
            binding.tvBorrowDate.text = "Pinjam: ${item.borrowDate}"
            binding.tvDueDate.text = "Tempo: ${item.returnDate}"

            if (item.status == "borrowed" && item.isOverdue()) {
                binding.tvOverdueWarning.visibility = View.VISIBLE
            } else {
                binding.tvOverdueWarning.visibility = View.GONE
            }
        }
    }
}
