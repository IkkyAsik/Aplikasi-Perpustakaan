package com.example.perpustakaan.worker

import android.content.ContentValues
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.perpustakaan.utils.NotificationHelper
import com.example.perpustakaan.database.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DueDateWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val db = DatabaseHelper(applicationContext)
        val fmt = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        val today = Date()
        val tomorrow = Date(today.time + 24L * 60 * 60 * 1000) // 1 day ahead

        val cursor = db.readableDatabase.rawQuery(
            "SELECT ${DatabaseHelper.COL_BORROW_ID}, ${DatabaseHelper.COL_BORROW_USER_ID}, ${DatabaseHelper.COL_BORROW_BOOK_ID}, ${DatabaseHelper.COL_BORROW_DUE_DATE} " +
                    "FROM ${DatabaseHelper.TABLE_BORROWINGS} " +
                    "WHERE ${DatabaseHelper.COL_BORROW_STATUS} = 'borrowed' " +
                    "AND ${DatabaseHelper.COL_BORROW_NOTIFIED} = 0 " +
                    "AND ${DatabaseHelper.COL_BORROW_DUE_DATE} IS NOT NULL",
            null
        )

        while (cursor.moveToNext()) {
            val borrowId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BORROW_ID))
            val bookId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BORROW_BOOK_ID))
            val dueStr = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BORROW_DUE_DATE))
            val dueDate = fmt.parse(dueStr) ?: continue
            // If due date is today or tomorrow (<= tomorrow), send notification
            if (!dueDate.after(tomorrow)) {
                // Send notification
                NotificationHelper.showDueDateNotification(
                    applicationContext,
                    "Pengingat Jatuh Tempo",
                    "Buku yang Anda pinjam akan jatuh tempo besok.",
                    bookId
                )
                // Mark as notified
                val cv = ContentValues().apply { put(DatabaseHelper.COL_BORROW_NOTIFIED, 1) }
                db.writableDatabase.update(
                    DatabaseHelper.TABLE_BORROWINGS,
                    cv,
                    "${DatabaseHelper.COL_BORROW_ID} = ?",
                    arrayOf(borrowId.toString())
                )
            }
        }
        cursor.close()
        return Result.success()
    }
}
