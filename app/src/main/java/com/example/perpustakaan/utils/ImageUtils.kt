package com.example.perpustakaan.utils

import android.content.Context
import android.graphics.Color
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.perpustakaan.R

object ImageUtils {
    fun loadBookCover(context: Context, imageUrl: String, coverColor: String, imageView: ImageView) {
        if (imageUrl.isNotEmpty()) {
            if (imageUrl.startsWith("http")) {
                // Load from Web
                Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_book_placeholder)
                    .error(R.drawable.ic_book_placeholder)
                    .into(imageView)
                imageView.setBackgroundColor(Color.TRANSPARENT)
            } else {
                // Try load from drawable resources
                val resId = context.resources.getIdentifier(imageUrl, "drawable", context.packageName)
                if (resId != 0) {
                    Glide.with(context)
                        .load(resId)
                        .into(imageView)
                    imageView.setBackgroundColor(Color.TRANSPARENT)
                } else {
                    fallbackToColor(coverColor, imageView)
                }
            }
        } else {
            fallbackToColor(coverColor, imageView)
        }
    }

    private fun fallbackToColor(coverColor: String, imageView: ImageView) {
        try {
            imageView.setBackgroundColor(Color.parseColor(coverColor))
        } catch (e: Exception) {
            imageView.setBackgroundColor(Color.parseColor("#2980B9"))
        }
        imageView.setImageResource(R.drawable.ic_book_placeholder)
    }
}
