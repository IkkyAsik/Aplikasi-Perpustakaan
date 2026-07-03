package com.example.perpustakaan.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.perpustakaan.databinding.ItemBorrowingBinding
import com.example.perpustakaan.model.Borrowing
import com.example.perpustakaan.utils.ImageUtils

class BorrowingAdapter(
    private val onReturnClick: (Borrowing) -> Unit
) : ListAdapter<Borrowing, BorrowingAdapter.BorrowingViewHolder>(BorrowingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BorrowingViewHolder {
        val binding = ItemBorrowingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BorrowingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BorrowingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BorrowingViewHolder(private val binding: ItemBorrowingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(borrowing: Borrowing) {
            binding.tvBorrowTitle.text = borrowing.bookTitle
            binding.tvBorrowAuthor.text = borrowing.bookAuthor
            
            ImageUtils.loadBookCover(binding.root.context, borrowing.coverImage, borrowing.coverColor, borrowing.bookTitle, borrowing.bookAuthor, binding.ivCoverImage)

            binding.tvBorrowDate.text = "Dipinjam: ${borrowing.borrowDate}"
            // Calculate days left until due date
            val daysLeft = try {
                val fmt = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale("id", "ID"))
                val due = fmt.parse(borrowing.dueDate)
                if (due == null) {
                    null
                } else {
                    val diffMs = due.time - java.util.Date().time
                    java.util.concurrent.TimeUnit.MILLISECONDS.toDays(diffMs)
                }
            } catch (e: Exception) { null }
            if (daysLeft != null && daysLeft >= 0) {
                binding.tvReturnDate.text = "Jatuh Tempo: ${borrowing.returnDate} (${daysLeft} hari lagi)"
            } else {
                binding.tvReturnDate.text = "Jatuh Tempo: ${borrowing.returnDate}"
            }
            if (borrowing.status == "borrowed") {
                // Check overdue
                if (borrowing.isOverdue()) {
                    binding.tvStatus.text = "Overdue"
                    binding.tvStatus.setBackgroundColor(Color.parseColor("#E74C3C")) // red
                    binding.tvStatus.setTextColor(Color.WHITE)
                } else {
                    binding.tvStatus.text = "Dipinjam"
                    binding.tvStatus.setBackgroundColor(Color.parseColor("#2980B9"))
                    binding.tvStatus.setTextColor(Color.WHITE)
                }
                binding.btnReturn.visibility = View.VISIBLE
                binding.btnReturn.setOnClickListener { onReturnClick(borrowing) }
            } else {
                binding.tvStatus.text = "Dikembalikan"
                binding.tvStatus.setBackgroundColor(Color.parseColor("#27AE60"))
                binding.tvStatus.setTextColor(Color.WHITE)
                binding.btnReturn.visibility = View.GONE
            }
        }
    }

    class BorrowingDiffCallback : DiffUtil.ItemCallback<Borrowing>() {
        override fun areItemsTheSame(oldItem: Borrowing, newItem: Borrowing) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Borrowing, newItem: Borrowing) = oldItem == newItem
    }
}
