package com.example.perpustakaan.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.perpustakaan.R
import com.example.perpustakaan.BookDetailActivity

object NotificationHelper {
    private const val CHANNEL_ID = "CHANNEL_DUE_DATE"
    private const val CHANNEL_NAME = "Notifikasi Jatuh Tempo"
    private const val CHANNEL_DESC = "Pengingat pengembalian buku yang mendekati jatuh tempo"

    fun createChannel(context: Context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESC
                enableLights(true)
                lightColor = android.graphics.Color.parseColor("#F1C40F")
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun showDueDateNotification(context: Context, title: String, message: String, bookId: Int) {
        val intent = Intent(context, BookDetailActivity::class.java).apply {
            putExtra("BOOK_ID", bookId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            bookId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_book_placeholder)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(bookId, notification)
    }
}
