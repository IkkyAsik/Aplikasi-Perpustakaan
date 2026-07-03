package com.example.perpustakaan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.perpustakaan.databinding.ItemReviewBinding
import com.example.perpustakaan.model.Review

class ReviewAdapter(
    private val onEdit: (Review) -> Unit,
    private val onDelete: (Review) -> Unit,
    private val canModify: (Review) -> Boolean
) : RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    private var reviews = listOf<Review>()

    fun submitList(list: List<Review>) {
        reviews = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = reviews.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review = reviews[position]
        holder.binding.tvReviewerName.text = review.userName
        holder.binding.tvRatingValue.text = String.format("%.1f ★", review.rating)
        holder.binding.tvReviewComment.text = review.comment.ifEmpty { "(Tidak ada komentar)" }
        holder.binding.tvReviewDate.text = review.date

        if (canModify(review)) {
            holder.binding.ivEdit.visibility = View.VISIBLE
            holder.binding.ivDelete.visibility = View.VISIBLE
            holder.binding.ivEdit.setOnClickListener { onEdit(review) }
            holder.binding.ivDelete.setOnClickListener { onDelete(review) }
        } else {
            holder.binding.ivEdit.visibility = View.GONE
            holder.binding.ivDelete.visibility = View.GONE
        }
    }
}
