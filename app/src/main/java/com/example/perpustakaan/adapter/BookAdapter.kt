package com.example.perpustakaan.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.perpustakaan.databinding.ItemBookBinding
import com.example.perpustakaan.model.Book
import com.example.perpustakaan.utils.ImageUtils

class BookAdapter(
    private val onBookClick: (Book) -> Unit
) : ListAdapter<Book, BookAdapter.BookViewHolder>(BookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BookViewHolder(private val binding: ItemBookBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(book: Book) {
            binding.tvBookTitle.text = book.title
            binding.tvBookAuthor.text = book.author
            binding.tvBookCategory.text = book.category

            ImageUtils.loadBookCover(binding.root.context, book.coverImage, book.coverColor, book.title, book.author, binding.ivCoverImage)

            if (book.availableCopies > 0) {
                binding.tvAvailable.text = "${book.availableCopies} tersedia"
                binding.tvAvailable.setTextColor(Color.parseColor("#27AE60"))
            } else {
                binding.tvAvailable.text = "Tidak tersedia"
                binding.tvAvailable.setTextColor(Color.parseColor("#E74C3C"))
            }

            binding.root.setOnClickListener { onBookClick(book) }
        }
    }

    class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Book, newItem: Book) = oldItem == newItem
    }
}
