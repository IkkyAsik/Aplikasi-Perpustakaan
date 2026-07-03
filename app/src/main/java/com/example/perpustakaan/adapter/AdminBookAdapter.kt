package com.example.perpustakaan.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.perpustakaan.databinding.ItemAdminBookBinding
import com.example.perpustakaan.model.Book
import com.example.perpustakaan.utils.ImageUtils

class AdminBookAdapter(
    private val onEditClick: (Book) -> Unit,
    private val onDeleteClick: (Book) -> Unit
) : ListAdapter<Book, AdminBookAdapter.AdminBookViewHolder>(AdminBookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminBookViewHolder {
        val binding = ItemAdminBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdminBookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdminBookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AdminBookViewHolder(private val binding: ItemAdminBookBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(book: Book) {
            binding.tvTitle.text = book.title
            binding.tvAuthor.text = book.author

            ImageUtils.loadBookCover(binding.root.context, book.coverImage, book.coverColor, book.title, book.author, binding.ivCoverImage)

            binding.tvAvailable.text = "${book.availableCopies} dari ${book.totalCopies} tersedia"
            if (book.availableCopies > 0) {
                binding.tvAvailable.setTextColor(Color.parseColor("#27AE60"))
            } else {
                binding.tvAvailable.setTextColor(Color.parseColor("#E74C3C"))
            }

            binding.btnEdit.setOnClickListener { onEditClick(book) }
            binding.btnDelete.setOnClickListener { onDeleteClick(book) }
        }
    }

    class AdminBookDiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Book, newItem: Book) = oldItem == newItem
    }
}
