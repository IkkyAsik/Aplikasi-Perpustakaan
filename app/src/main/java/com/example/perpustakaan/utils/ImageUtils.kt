package com.example.perpustakaan.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.perpustakaan.R

object ImageUtils {
    /**
     * Load a book cover image into the provided ImageView. If the image URL is empty or cannot be loaded,
     * a dynamically generated placeholder featuring the book title and author initials will be displayed.
     */
    fun loadBookCover(
        context: Context,
        imageUrl: String,
        coverColor: String,
        title: String,
        author: String,
        imageView: ImageView
    ) {
        if (imageUrl.isNotEmpty()) {
            if (imageUrl.startsWith("http")) {
                // Load from Web
                Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_book_placeholder)
                    .error(R.drawable.ic_book_placeholder)
                    .into(imageView)
                imageView.setBackgroundColor(Color.TRANSPARENT)
                return
            } else {
                // Try load from drawable resources
                val resId = context.resources.getIdentifier(imageUrl, "drawable", context.packageName)
                if (resId != 0) {
                    Glide.with(context)
                        .load(resId)
                        .into(imageView)
                    imageView.setBackgroundColor(Color.TRANSPARENT)
                    return
                }
            }
        }
        // Fallback to generated placeholder
        generatePlaceholderCover(context, title, author, coverColor, imageView)
    }

    /**
     * Legacy overload kept for compatibility – simply forwards to the new implementation using title/author.
     */
    fun loadBookCover(
        context: Context,
        imageUrl: String,
        coverColor: String,
        imageView: ImageView
    ) {
        // Use empty strings for title/author when not provided
        loadBookCover(context, imageUrl, coverColor, "", "", imageView)
    }

    /**
     * Generate a bitmap placeholder with the book's title and author initials on a colored background.
     */
    private fun generatePlaceholderCover(
        context: Context,
        title: String,
        author: String,
        coverColor: String,
        imageView: ImageView
    ) {
        // Use a portrait aspect ratio typical of book covers
        val width = 400
        val height = 600
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        // Gradient background based on coverColor (fallback to default)
        val startColor = try { Color.parseColor(coverColor) } catch (e: Exception) { Color.parseColor("#2980B9") }
        val endColor = Color.BLACK
        val gradient = android.graphics.LinearGradient(
            0f, 0f, width.toFloat(), height.toFloat(), startColor, endColor, android.graphics.Shader.TileMode.CLAMP
        )
        val bgPaint = android.graphics.Paint().apply { shader = gradient }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
        // Semi‑transparent overlay to suggest a page
        val overlayPaint = android.graphics.Paint().apply {
            color = Color.WHITE
            alpha = 30 // ~12% opacity
        }
        val inset = width * 0.05f
        canvas.drawRoundRect(inset, inset, width - inset, height - inset, 20f, 20f, overlayPaint)
        // Drop shadow for depth
        val shadowPaint = android.graphics.Paint().apply {
            color = Color.BLACK
            alpha = 80
        }
        val shadowOffset = 8f
        canvas.drawRect(shadowOffset, shadowOffset, width.toFloat() + shadowOffset, height.toFloat() + shadowOffset, shadowPaint)
        // Border to frame the cover
        val borderPaint = android.graphics.Paint().apply {
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = 8f
            color = Color.WHITE
            isAntiAlias = true
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), borderPaint)
        // Title text – larger, bold, centered
        val titlePaint = android.graphics.Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            textAlign = android.graphics.Paint.Align.CENTER
            textSize = width * 0.12f
            typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.BOLD)
        }
        val displayTitle = if (title.length > 30) title.substring(0, 27) + "…" else title
        canvas.drawText(displayTitle, (width / 2).toFloat(), (height * 0.4).toFloat(), titlePaint)
        // Author text – smaller, subtle, near bottom
        val authorPaint = android.graphics.Paint().apply {
            isAntiAlias = true
            color = Color.LTGRAY
            textAlign = android.graphics.Paint.Align.CENTER
            textSize = width * 0.06f
            typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.NORMAL)
        }
        val displayAuthor = author.ifBlank { "" }
        canvas.drawText(displayAuthor, (width / 2).toFloat(), (height * 0.85).toFloat(), authorPaint)
        // Load bitmap into ImageView via Glide
        Glide.with(context).load(bitmap).into(imageView)
    }
}
