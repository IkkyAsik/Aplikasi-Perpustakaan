package com.example.perpustakaan.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.perpustakaan.model.Book
import com.example.perpustakaan.model.Borrowing
import com.example.perpustakaan.model.Review
import com.example.perpustakaan.model.User
import com.example.perpustakaan.model.MemberBorrowInfo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "perpustakaan.db"
        const val DATABASE_VERSION = 4

        const val TABLE_USERS = "users"
        const val COL_USER_ID = "id"
        const val COL_USER_NAME = "name"
        const val COL_USER_EMAIL = "email"
        const val COL_USER_PASSWORD = "password"
        const val COL_USER_PHONE = "phone"
        const val COL_USER_CREATED_AT = "created_at"

        const val TABLE_BOOKS = "books"
        const val COL_BOOK_ID = "id"
        const val COL_BOOK_TITLE = "title"
        const val COL_BOOK_AUTHOR = "author"
        const val COL_BOOK_CATEGORY = "category"
        const val COL_BOOK_DESCRIPTION = "description"
        const val COL_BOOK_YEAR = "year"
        const val COL_BOOK_TOTAL_COPIES = "total_copies"
        const val COL_BOOK_AVAILABLE_COPIES = "available_copies"
        const val COL_BOOK_ISBN = "isbn"
        const val COL_BOOK_COVER_COLOR = "cover_color"
        const val COL_BOOK_COVER_IMAGE = "cover_image"

        const val TABLE_BORROWINGS = "borrowings"
        const val COL_BORROW_ID = "id"
        const val COL_BORROW_USER_ID = "user_id"
        const val COL_BORROW_BOOK_ID = "book_id"
        const val COL_BORROW_DATE = "borrow_date"
        const val COL_BORROW_RETURN_DATE = "return_date"
        const val COL_BORROW_STATUS = "status"
        const val COL_BORROW_DUE_DATE = "due_date"
        const val COL_BORROW_NOTIFIED = "notified"
        const val COL_BORROW_ACTUAL_RETURN_DATE = "actual_return_date"

        const val TABLE_FAVORITES = "favorites"
        const val COL_FAV_ID = "id"
        const val COL_FAV_USER_ID = "user_id"
        const val COL_FAV_BOOK_ID = "book_id"

        const val TABLE_REVIEWS = "reviews"
        const val COL_REVIEW_ID = "id"
        const val COL_REVIEW_USER_ID = "user_id"
        const val COL_REVIEW_BOOK_ID = "book_id"
        const val COL_REVIEW_RATING = "rating"
        const val COL_REVIEW_COMMENT = "comment"
        const val COL_REVIEW_DATE = "review_date"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_USERS (" +
            "$COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "$COL_USER_NAME TEXT NOT NULL, " +
            "$COL_USER_EMAIL TEXT NOT NULL UNIQUE, " +
            "$COL_USER_PASSWORD TEXT NOT NULL, " +
            "$COL_USER_PHONE TEXT, " +
            "$COL_USER_CREATED_AT TEXT)"
        )

        db.execSQL(
            "CREATE TABLE $TABLE_BOOKS (" +
            "$COL_BOOK_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "$COL_BOOK_TITLE TEXT NOT NULL, " +
            "$COL_BOOK_AUTHOR TEXT NOT NULL, " +
            "$COL_BOOK_CATEGORY TEXT NOT NULL, " +
            "$COL_BOOK_DESCRIPTION TEXT, " +
            "$COL_BOOK_YEAR TEXT, " +
            "$COL_BOOK_TOTAL_COPIES INTEGER DEFAULT 3, " +
            "$COL_BOOK_AVAILABLE_COPIES INTEGER DEFAULT 3, " +
            "$COL_BOOK_ISBN TEXT, " +
            "$COL_BOOK_COVER_COLOR TEXT DEFAULT '#2980B9', " +
            "$COL_BOOK_COVER_IMAGE TEXT)"
        )

        db.execSQL(
            "CREATE TABLE $TABLE_BORROWINGS (" +
            "$COL_BORROW_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "$COL_BORROW_USER_ID INTEGER NOT NULL, " +
            "$COL_BORROW_BOOK_ID INTEGER NOT NULL, " +
            "$COL_BORROW_DATE TEXT, " +
            "$COL_BORROW_RETURN_DATE TEXT, " +
            "$COL_BORROW_DUE_DATE TEXT, " +
            "$COL_BORROW_STATUS TEXT DEFAULT 'borrowed', " +
            "$COL_BORROW_ACTUAL_RETURN_DATE TEXT, " +
            "$COL_BORROW_NOTIFIED INTEGER DEFAULT 0)"
        )

        db.execSQL(
            "CREATE TABLE $TABLE_FAVORITES (" +
            "$COL_FAV_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "$COL_FAV_USER_ID INTEGER NOT NULL, " +
            "$COL_FAV_BOOK_ID INTEGER NOT NULL, " +
            "UNIQUE($COL_FAV_USER_ID, $COL_FAV_BOOK_ID))"
        )

        db.execSQL(
            "CREATE TABLE $TABLE_REVIEWS (" +
            "$COL_REVIEW_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "$COL_REVIEW_USER_ID INTEGER NOT NULL, " +
            "$COL_REVIEW_BOOK_ID INTEGER NOT NULL, " +
            "$COL_REVIEW_RATING REAL NOT NULL, " +
            "$COL_REVIEW_COMMENT TEXT, " +
            "$COL_REVIEW_DATE TEXT)"
        )

        insertSampleDataIfNeeded(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Preserve existing data. Future schema migrations can be added here.
        // Example for adding a new column:
        // if (oldVersion < 5) {
        //     db.execSQL("ALTER TABLE $TABLE_BOOKS ADD COLUMN new_column TEXT")
        // }
    }

    // Insert sample data only when the books table is empty
    private fun insertSampleDataIfNeeded(db: SQLiteDatabase) {
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_BOOKS", null)
        var isEmpty = true
        if (cursor.moveToFirst()) {
            isEmpty = cursor.getInt(0) == 0
        }
        cursor.close()
        if (isEmpty) {
            insertSampleData(db)
        }
    }

    private fun insertSampleData(db: SQLiteDatabase) {
        val admin = ContentValues().apply {
            put(COL_USER_NAME, "Admin Perpustakaan")
            put(COL_USER_EMAIL, "admin@perpustakaan.com")
            put(COL_USER_PASSWORD, "admin123")
            put(COL_USER_PHONE, "081234567890")
            put(COL_USER_CREATED_AT, "01 Januari 2024")
        }
        db.insert(TABLE_USERS, null, admin)

        val books = listOf(
            arrayOf("Bumi Manusia", "Pramoedya Ananta Toer", "Sastra",
                "Novel sejarah tentang perjuangan Minke, seorang pribumi Jawa di era kolonial Belanda. Kisah cinta, perjuangan, dan penindasan yang luar biasa kuat.", "1980", "3", "978-979-22-9751-5", "#8E44AD", "cover_bumi_manusia"),
            arrayOf("Laskar Pelangi", "Andrea Hirata", "Novel",
                "Kisah inspiratif tentang sepuluh anak Melayu Belitung yang berjuang mendapatkan pendidikan di sekolah Muhammadiyah yang hampir roboh.", "2005", "4", "978-979-3062-79-9", "#E67E22", "cover_laskar_pelangi"),
            arrayOf("Negeri 5 Menara", "Ahmad Fuadi", "Novel",
                "Novel inspiratif berlatar pesantren Pondok Madani. Kisah Alif dan kelima sahabatnya dari berbagai penjuru nusantara yang memiliki cita-cita tinggi.", "2009", "3", "978-602-03-1614-9", "#2980B9", "cover_negeri_5_menara"),
            arrayOf("Dilan: Dia adalah Dilanku tahun 1990", "Pidi Baiq", "Novel",
                "Kisah cinta remaja SMA antara Milea dan Dilan di Bandung tahun 1990. Dilan adalah pria yang unik, romantis, dan penuh kejutan dalam setiap langkahnya.", "2014", "3", "978-602-1637-24-5", "#C0392B", "cover_dilan_1990"),
            arrayOf("Perahu Kertas", "Dee Lestari", "Novel",
                "Kisah cinta Kugy dan Keenan yang dihubungkan oleh seni dan perahu kertas. Novel tentang panggilan jiwa dan keberanian mengikuti hati nurani.", "2009", "2", "978-979-2402-46-6", "#16A085", "cover_perahu_kertas"),
            arrayOf("Filosofi Teras", "Henry Manampiring", "Filsafat",
                "Buku yang memperkenalkan filosofi Stoa dari Yunani-Romawi kuno dan mengaplikasikannya pada kehidupan modern untuk mengatasi emosi negatif dan kekhawatiran.", "2018", "3", "978-602-06-1860-1", "#154360", "cover_filosofi_teras"),
            arrayOf("Sapiens: Riwayat Singkat Umat Manusia", "Yuval Noah Harari", "Sains",
                "Buku yang menelusuri perjalanan Homo sapiens dari zaman purba hingga era modern, mengeksplorasi bagaimana manusia menjadi spesies yang menguasai dunia.", "2014", "2", "978-979-91-0847-5", "#6E2F1A", ""),
            arrayOf("Sejarah Indonesia Modern", "M.C. Ricklefs", "Sejarah",
                "Kajian komprehensif tentang sejarah Indonesia dari masa kerajaan awal hingga era reformasi, ditulis oleh sejarawan terkemuka.", "2008", "2", "978-979-22-2403-0", "#27AE60", ""),
            arrayOf("Rich Dad Poor Dad", "Robert T. Kiyosaki", "Bisnis",
                "Buku tentang pendidikan keuangan yang membandingkan pola pikir ayah kaya dan ayah miskin dalam mengelola uang, aset, dan kewajiban finansial.", "1997", "3", "978-1-61268-110-7", "#D4AC0D", ""),
            arrayOf("Clean Code", "Robert C. Martin", "Teknologi",
                "Panduan untuk menulis kode yang bersih, mudah dibaca, dan mudah dipelihara. Buku wajib bagi setiap programmer yang ingin meningkatkan kualitas kodenya.", "2008", "2", "978-0-13-235088-4", "#2C3E50", ""),
            arrayOf("Atomic Habits", "James Clear", "Bisnis",
                "Panduan praktis untuk membangun kebiasaan baik dan menghilangkan kebiasaan buruk melalui perubahan kecil yang memberikan hasil luar biasa.", "2018", "3", "978-0-7352-1129-2", "#E74C3C", ""),
            arrayOf("Cantik Itu Luka", "Eka Kurniawan", "Sastra",
                "Novel yang mengisahkan tiga generasi keluarga dari zaman kolonial hingga modern, penuh unsur magis, tragedi, dan kritik sosial yang tajam.", "2002", "2", "978-979-1503-43-8", "#8E44AD", ""),
            arrayOf("Harry Potter dan Batu Bertuah", "J.K. Rowling", "Novel",
                "Kisah petualangan Harry Potter, seorang anak yatim piatu yang menemukan bahwa dirinya adalah seorang penyihir dan diterima di Sekolah Sihir Hogwarts.", "1997", "4", "978-979-22-2800-7", "#C0392B", ""),
            arrayOf("The Pragmatic Programmer", "David Thomas & Andrew Hunt", "Teknologi",
                "Buku klasik tentang pengembangan perangkat lunak berisi tips dan filosofi untuk menjadi programmer yang lebih efektif dan pragmatis dalam pekerjaan sehari-hari.", "1999", "2", "978-0-201-61622-4", "#2C3E50", ""),
            arrayOf("Matematika untuk SMA", "Tim Penulis", "Pendidikan",
                "Buku pelajaran matematika komprehensif untuk tingkat SMA yang mencakup aljabar, geometri, trigonometri, statistika, dan kalkulus dasar.", "2020", "5", "978-602-427-812-8", "#27AE60", "")
        )

        books.forEach { b ->
            val cv = ContentValues().apply {
                put(COL_BOOK_TITLE, b[0])
                put(COL_BOOK_AUTHOR, b[1])
                put(COL_BOOK_CATEGORY, b[2])
                put(COL_BOOK_DESCRIPTION, b[3])
                put(COL_BOOK_YEAR, b[4])
                put(COL_BOOK_TOTAL_COPIES, b[5].toInt())
                put(COL_BOOK_AVAILABLE_COPIES, b[5].toInt())
                put(COL_BOOK_ISBN, b[6])
                put(COL_BOOK_COVER_COLOR, b[7])
                put(COL_BOOK_COVER_IMAGE, b[8])
            }
            db.insert(TABLE_BOOKS, null, cv)
        }
    }

    // ─── USER OPERATIONS ────────────────────────────────────────

    fun addUser(user: User): Long {
        val cv = ContentValues().apply {
            put(COL_USER_NAME, user.name)
            put(COL_USER_EMAIL, user.email)
            put(COL_USER_PASSWORD, user.password)
            put(COL_USER_PHONE, user.phone)
            put(COL_USER_CREATED_AT, user.createdAt)
        }
        return writableDatabase.insert(TABLE_USERS, null, cv)
    }

    fun loginUser(email: String, password: String): User? {
        val cursor = readableDatabase.query(
            TABLE_USERS, null,
            "$COL_USER_EMAIL = ? AND $COL_USER_PASSWORD = ?",
            arrayOf(email, password), null, null, null
        )
        return if (cursor.moveToFirst()) cursorToUser(cursor).also { cursor.close() }
        else { cursor.close(); null }
    }

    fun isEmailExists(email: String): Boolean {
        val cursor = readableDatabase.query(
            TABLE_USERS, arrayOf(COL_USER_ID),
            "$COL_USER_EMAIL = ?", arrayOf(email), null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun getTotalUsersCount(): Int {
        val cursor = readableDatabase.rawQuery("SELECT COUNT(*) FROM $TABLE_USERS WHERE $COL_USER_EMAIL != 'admin@perpustakaan.com'", null)
        val count = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()
        return count
    }

    fun getAllMembersWithActiveBorrowCount(): List<MemberBorrowInfo> {
        val list = mutableListOf<MemberBorrowInfo>()
        val query = "SELECT u.$COL_USER_ID, u.$COL_USER_NAME, u.$COL_USER_EMAIL, u.$COL_USER_PHONE, u.$COL_USER_CREATED_AT, " +
                    "(SELECT COUNT(*) FROM $TABLE_BORROWINGS b WHERE b.$COL_BORROW_USER_ID = u.$COL_USER_ID AND b.$COL_BORROW_STATUS = 'borrowed') AS active_count " +
                    "FROM $TABLE_USERS u " +
                    "WHERE u.$COL_USER_EMAIL != 'admin@perpustakaan.com' " +
                    "ORDER BY u.$COL_USER_NAME"
        val cursor = readableDatabase.rawQuery(query, null)
        while (cursor.moveToNext()) {
            list.add(
                MemberBorrowInfo(
                    id = cursor.getInt(0),
                    name = cursor.getString(1),
                    email = cursor.getString(2),
                    phone = cursor.getString(3) ?: "",
                    createdAt = cursor.getString(4) ?: "",
                    activeBorrowCount = cursor.getInt(5)
                )
            )
        }
        cursor.close()
        return list
    }

    private fun cursorToUser(c: Cursor) = User(
        id = c.getInt(c.getColumnIndexOrThrow(COL_USER_ID)),
        name = c.getString(c.getColumnIndexOrThrow(COL_USER_NAME)),
        email = c.getString(c.getColumnIndexOrThrow(COL_USER_EMAIL)),
        password = c.getString(c.getColumnIndexOrThrow(COL_USER_PASSWORD)),
        phone = c.getString(c.getColumnIndexOrThrow(COL_USER_PHONE)) ?: "",
        createdAt = c.getString(c.getColumnIndexOrThrow(COL_USER_CREATED_AT)) ?: ""
    )

    // ─── BOOK OPERATIONS ────────────────────────────────────────

    fun getAllBooks(): List<Book> {
        val list = mutableListOf<Book>()
        val cursor = readableDatabase.query(TABLE_BOOKS, null, null, null, null, null, COL_BOOK_TITLE)
        while (cursor.moveToNext()) list.add(cursorToBook(cursor))
        cursor.close()
        return list
    }

    fun getBooksByCategory(category: String): List<Book> {
        val list = mutableListOf<Book>()
        val cursor = readableDatabase.query(
            TABLE_BOOKS, null,
            "$COL_BOOK_CATEGORY = ?", arrayOf(category), null, null, COL_BOOK_TITLE
        )
        while (cursor.moveToNext()) list.add(cursorToBook(cursor))
        cursor.close()
        return list
    }

    fun searchBooks(query: String): List<Book> {
        val list = mutableListOf<Book>()
        val cursor = readableDatabase.query(
            TABLE_BOOKS, null,
            "$COL_BOOK_TITLE LIKE ? OR $COL_BOOK_AUTHOR LIKE ?",
            arrayOf("%$query%", "%$query%"), null, null, COL_BOOK_TITLE
        )
        while (cursor.moveToNext()) list.add(cursorToBook(cursor))
        cursor.close()
        return list
    }

    fun getBookById(id: Int): Book? {
        val cursor = readableDatabase.query(
            TABLE_BOOKS, null,
            "$COL_BOOK_ID = ?", arrayOf(id.toString()), null, null, null
        )
        return if (cursor.moveToFirst()) cursorToBook(cursor).also { cursor.close() }
        else { cursor.close(); null }
    }

    fun getBooksFiltered(category: String, onlyAvailable: Boolean, sortBy: String): List<Book> {
        val selection = mutableListOf<String>()
        val selectionArgs = mutableListOf<String>()

        if (category != "Semua") {
            selection.add("$COL_BOOK_CATEGORY = ?")
            selectionArgs.add(category)
        }
        if (onlyAvailable) {
            selection.add("$COL_BOOK_AVAILABLE_COPIES > 0")
        }

        val orderBy = when (sortBy) {
            "Z-A" -> "$COL_BOOK_TITLE DESC"
            "Terbaru" -> "$COL_BOOK_YEAR DESC"
            "Terlama" -> "$COL_BOOK_YEAR ASC"
            else -> COL_BOOK_TITLE // A-Z default
        }

        val list = mutableListOf<Book>()
        val cursor = readableDatabase.query(
            TABLE_BOOKS, null,
            if (selection.isEmpty()) null else selection.joinToString(" AND "),
            if (selectionArgs.isEmpty()) null else selectionArgs.toTypedArray(),
            null, null, orderBy
        )
        while (cursor.moveToNext()) list.add(cursorToBook(cursor))
        cursor.close()
        return list
    }

    fun getMostBorrowedBooks(limit: Int = 5): List<Pair<String, Int>> {
        val result = mutableListOf<Pair<String, Int>>()
        val cursor = readableDatabase.rawQuery(
            "SELECT bk.$COL_BOOK_TITLE, COUNT(b.$COL_BORROW_ID) as borrow_count " +
            "FROM $TABLE_BORROWINGS b JOIN $TABLE_BOOKS bk ON b.$COL_BORROW_BOOK_ID = bk.$COL_BOOK_ID " +
            "GROUP BY b.$COL_BORROW_BOOK_ID ORDER BY borrow_count DESC LIMIT ?",
            arrayOf(limit.toString())
        )
        while (cursor.moveToNext()) {
            result.add(Pair(cursor.getString(0), cursor.getInt(1)))
        }
        cursor.close()
        return result
    }

    fun getTotalBooksCount(): Int {
        val cursor = readableDatabase.rawQuery("SELECT COUNT(*) FROM $TABLE_BOOKS", null)
        val count = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()
        return count
    }

    fun getTotalActiveBorrowingsCount(): Int {
        val cursor = readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM $TABLE_BORROWINGS WHERE $COL_BORROW_STATUS = 'borrowed'", null)
        val count = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()
        return count
    }

    private fun updateAvailability(bookId: Int, copies: Int) {
        val cv = ContentValues().apply { put(COL_BOOK_AVAILABLE_COPIES, copies) }
        writableDatabase.update(TABLE_BOOKS, cv, "$COL_BOOK_ID = ?", arrayOf(bookId.toString()))
    }

    private fun cursorToBook(c: Cursor) = Book(
        id = c.getInt(c.getColumnIndexOrThrow(COL_BOOK_ID)),
        title = c.getString(c.getColumnIndexOrThrow(COL_BOOK_TITLE)),
        author = c.getString(c.getColumnIndexOrThrow(COL_BOOK_AUTHOR)),
        category = c.getString(c.getColumnIndexOrThrow(COL_BOOK_CATEGORY)),
        description = c.getString(c.getColumnIndexOrThrow(COL_BOOK_DESCRIPTION)) ?: "",
        year = c.getString(c.getColumnIndexOrThrow(COL_BOOK_YEAR)) ?: "",
        totalCopies = c.getInt(c.getColumnIndexOrThrow(COL_BOOK_TOTAL_COPIES)),
        availableCopies = c.getInt(c.getColumnIndexOrThrow(COL_BOOK_AVAILABLE_COPIES)),
        isbn = c.getString(c.getColumnIndexOrThrow(COL_BOOK_ISBN)) ?: "",
        coverColor = c.getString(c.getColumnIndexOrThrow(COL_BOOK_COVER_COLOR)) ?: "#2980B9",
        coverImage = c.getString(c.getColumnIndexOrThrow(COL_BOOK_COVER_IMAGE)) ?: ""
    )

    fun addBook(book: Book): Long {
        val cv = ContentValues().apply {
            put(COL_BOOK_TITLE, book.title)
            put(COL_BOOK_AUTHOR, book.author)
            put(COL_BOOK_CATEGORY, book.category)
            put(COL_BOOK_DESCRIPTION, book.description)
            put(COL_BOOK_YEAR, book.year)
            put(COL_BOOK_TOTAL_COPIES, book.totalCopies)
            put(COL_BOOK_AVAILABLE_COPIES, book.availableCopies)
            put(COL_BOOK_ISBN, book.isbn)
            put(COL_BOOK_COVER_COLOR, book.coverColor)
            put(COL_BOOK_COVER_IMAGE, book.coverImage)
        }
        return writableDatabase.insert(TABLE_BOOKS, null, cv)
    }

    fun updateBook(book: Book): Int {
        val cv = ContentValues().apply {
            put(COL_BOOK_TITLE, book.title)
            put(COL_BOOK_AUTHOR, book.author)
            put(COL_BOOK_CATEGORY, book.category)
            put(COL_BOOK_DESCRIPTION, book.description)
            put(COL_BOOK_YEAR, book.year)
            put(COL_BOOK_TOTAL_COPIES, book.totalCopies)
            put(COL_BOOK_AVAILABLE_COPIES, book.availableCopies)
            put(COL_BOOK_ISBN, book.isbn)
            put(COL_BOOK_COVER_COLOR, book.coverColor)
            put(COL_BOOK_COVER_IMAGE, book.coverImage)
        }
        return writableDatabase.update(TABLE_BOOKS, cv, "$COL_BOOK_ID = ?", arrayOf(book.id.toString()))
    }

    // Update only the cover image for a specific book
    fun updateBookCover(bookId: Int, coverImage: String) {
        val cv = ContentValues().apply { put(COL_BOOK_COVER_IMAGE, coverImage) }
        writableDatabase.update(TABLE_BOOKS, cv, "$COL_BOOK_ID = ?", arrayOf(bookId.toString()))
    }

    fun deleteBook(bookId: Int): Int {
        writableDatabase.delete(TABLE_FAVORITES, "$COL_FAV_BOOK_ID = ?", arrayOf(bookId.toString()))
        writableDatabase.delete(TABLE_REVIEWS, "$COL_REVIEW_BOOK_ID = ?", arrayOf(bookId.toString()))
        writableDatabase.delete(TABLE_BORROWINGS, "$COL_BORROW_BOOK_ID = ?", arrayOf(bookId.toString()))
        return writableDatabase.delete(TABLE_BOOKS, "$COL_BOOK_ID = ?", arrayOf(bookId.toString()))
    }

    // ─── BORROWING OPERATIONS ───────────────────────────────────

    fun borrowBook(userId: Int, bookId: Int): Boolean {
        val book = getBookById(bookId) ?: return false
        if (book.availableCopies <= 0) return false

        val fmt = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        val now = Date()
        val returnDate = Date(now.time + 14L * 24 * 60 * 60 * 1000)
        val dueDate = Date(now.time + 13L * 24 * 60 * 60 * 1000)

        val cv = ContentValues().apply {
            put(COL_BORROW_USER_ID, userId)
            put(COL_BORROW_BOOK_ID, bookId)
            put(COL_BORROW_DATE, fmt.format(now))
            put(COL_BORROW_RETURN_DATE, fmt.format(returnDate))
            put(COL_BORROW_DUE_DATE, fmt.format(dueDate))
            put(COL_BORROW_STATUS, "borrowed")
        }
        val result = writableDatabase.insert(TABLE_BORROWINGS, null, cv)
        if (result > 0) updateAvailability(bookId, book.availableCopies - 1)
        return result > 0
    }

    fun returnBook(borrowingId: Int, bookId: Int): Boolean {
        val book = getBookById(bookId) ?: return false
        val fmt = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        val cv = ContentValues().apply {
            put(COL_BORROW_STATUS, "returned")
            put(COL_BORROW_ACTUAL_RETURN_DATE, fmt.format(Date()))
        }
        val rows = writableDatabase.update(
            TABLE_BORROWINGS, cv, "$COL_BORROW_ID = ?", arrayOf(borrowingId.toString())
        )
        if (rows > 0) updateAvailability(bookId, book.availableCopies + 1)
        return rows > 0
    }

    fun getActiveBorrowingForBook(userId: Int, bookId: Int): Borrowing? {
        val cursor = readableDatabase.rawQuery(
            "SELECT b.*, bk.$COL_BOOK_TITLE, bk.$COL_BOOK_AUTHOR, bk.$COL_BOOK_COVER_COLOR, bk.$COL_BOOK_COVER_IMAGE " +
            "FROM $TABLE_BORROWINGS b " +
            "JOIN $TABLE_BOOKS bk ON b.$COL_BORROW_BOOK_ID = bk.$COL_BOOK_ID " +
            "WHERE b.$COL_BORROW_USER_ID = ? AND b.$COL_BORROW_BOOK_ID = ? AND b.$COL_BORROW_STATUS = 'borrowed'",
            arrayOf(userId.toString(), bookId.toString())
        )
        return if (cursor.moveToFirst()) cursorToBorrowing(cursor).also { cursor.close() }
        else { cursor.close(); null }
    }

    fun getUserBorrowings(userId: Int): List<Borrowing> {
        val list = mutableListOf<Borrowing>()
        val cursor = readableDatabase.rawQuery(
            "SELECT b.*, bk.$COL_BOOK_TITLE, bk.$COL_BOOK_AUTHOR, bk.$COL_BOOK_COVER_COLOR, bk.$COL_BOOK_COVER_IMAGE " +
            "FROM $TABLE_BORROWINGS b " +
            "JOIN $TABLE_BOOKS bk ON b.$COL_BORROW_BOOK_ID = bk.$COL_BOOK_ID " +
            "WHERE b.$COL_BORROW_USER_ID = ? ORDER BY b.$COL_BORROW_ID DESC",
            arrayOf(userId.toString())
        )
        while (cursor.moveToNext()) list.add(cursorToBorrowing(cursor))
        cursor.close()
        return list
    }

    fun getAllBorrowings(): List<Borrowing> {
        val list = mutableListOf<Borrowing>()
        val cursor = readableDatabase.rawQuery(
            "SELECT b.*, bk.$COL_BOOK_TITLE, bk.$COL_BOOK_AUTHOR, bk.$COL_BOOK_COVER_COLOR, bk.$COL_BOOK_COVER_IMAGE " +
            "FROM $TABLE_BORROWINGS b " +
            "JOIN $TABLE_BOOKS bk ON b.$COL_BORROW_BOOK_ID = bk.$COL_BOOK_ID " +
            "ORDER BY b.$COL_BORROW_ID DESC", null
        )
        while (cursor.moveToNext()) list.add(cursorToBorrowing(cursor))
        cursor.close()
        return list
    }

    fun getOverdueBorrowings(userId: Int): List<Borrowing> {
        return getUserBorrowings(userId).filter { it.status == "borrowed" && it.isOverdue() }
    }

    fun getTotalBorrowedCount(userId: Int): Int {
        val cursor = readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM $TABLE_BORROWINGS WHERE $COL_BORROW_USER_ID = ?",
            arrayOf(userId.toString())
        )
        val count = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()
        return count
    }

    fun getActiveBorrowingsCount(userId: Int): Int {
        val cursor = readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM $TABLE_BORROWINGS WHERE $COL_BORROW_USER_ID = ? AND $COL_BORROW_STATUS = 'borrowed'",
            arrayOf(userId.toString())
        )
        val count = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()
        return count
    }

    private fun cursorToBorrowing(c: Cursor) = Borrowing(
        id = c.getInt(c.getColumnIndexOrThrow(COL_BORROW_ID)),
        userId = c.getInt(c.getColumnIndexOrThrow(COL_BORROW_USER_ID)),
        bookId = c.getInt(c.getColumnIndexOrThrow(COL_BORROW_BOOK_ID)),
        bookTitle = c.getString(c.getColumnIndexOrThrow(COL_BOOK_TITLE)) ?: "",
        bookAuthor = c.getString(c.getColumnIndexOrThrow(COL_BOOK_AUTHOR)) ?: "",
        coverColor = c.getString(c.getColumnIndexOrThrow(COL_BOOK_COVER_COLOR)) ?: "#2980B9",
        coverImage = c.getString(c.getColumnIndexOrThrow(COL_BOOK_COVER_IMAGE)) ?: "",
        borrowDate = c.getString(c.getColumnIndexOrThrow(COL_BORROW_DATE)) ?: "",
        returnDate = c.getString(c.getColumnIndexOrThrow(COL_BORROW_RETURN_DATE)) ?: "",
        dueDate = c.getString(c.getColumnIndexOrThrow(COL_BORROW_DUE_DATE)) ?: "",
        status = c.getString(c.getColumnIndexOrThrow(COL_BORROW_STATUS)) ?: "borrowed",
        notified = c.getInt(c.getColumnIndexOrThrow(COL_BORROW_NOTIFIED)) == 1,
        actualReturnDate = c.getString(c.getColumnIndexOrThrow(COL_BORROW_ACTUAL_RETURN_DATE))
    )

    // ─── FAVORITE OPERATIONS ───────────────────────────────────

    fun addFavorite(userId: Int, bookId: Int): Boolean {
        val cv = ContentValues().apply {
            put(COL_FAV_USER_ID, userId)
            put(COL_FAV_BOOK_ID, bookId)
        }
        return try {
            writableDatabase.insertOrThrow(TABLE_FAVORITES, null, cv) > 0
        } catch (e: Exception) { false }
    }

    fun removeFavorite(userId: Int, bookId: Int): Boolean {
        return writableDatabase.delete(
            TABLE_FAVORITES,
            "$COL_FAV_USER_ID = ? AND $COL_FAV_BOOK_ID = ?",
            arrayOf(userId.toString(), bookId.toString())
        ) > 0
    }

    fun isFavorite(userId: Int, bookId: Int): Boolean {
        val cursor = readableDatabase.query(
            TABLE_FAVORITES, arrayOf(COL_FAV_ID),
            "$COL_FAV_USER_ID = ? AND $COL_FAV_BOOK_ID = ?",
            arrayOf(userId.toString(), bookId.toString()), null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun getUserFavorites(userId: Int): List<Book> {
        val list = mutableListOf<Book>()
        val cursor = readableDatabase.rawQuery(
            "SELECT bk.* FROM $TABLE_BOOKS bk " +
            "JOIN $TABLE_FAVORITES f ON bk.$COL_BOOK_ID = f.$COL_FAV_BOOK_ID " +
            "WHERE f.$COL_FAV_USER_ID = ? ORDER BY bk.$COL_BOOK_TITLE",
            arrayOf(userId.toString())
        )
        while (cursor.moveToNext()) list.add(cursorToBook(cursor))
        cursor.close()
        return list
    }

    // ─── REVIEW OPERATIONS ─────────────────────────────────────

    fun addReview(review: Review): Boolean {
        val fmt = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        val cv = ContentValues().apply {
            put(COL_REVIEW_USER_ID, review.userId)
            put(COL_REVIEW_BOOK_ID, review.bookId)
            put(COL_REVIEW_RATING, review.rating)
            put(COL_REVIEW_COMMENT, review.comment)
            put(COL_REVIEW_DATE, fmt.format(Date()))
        }
        return writableDatabase.insert(TABLE_REVIEWS, null, cv) > 0
    }

    // Update an existing review (only by its owner)
    fun updateReview(review: Review): Boolean {
        val fmt = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        val cv = ContentValues().apply {
            put(COL_REVIEW_RATING, review.rating)
            put(COL_REVIEW_COMMENT, review.comment)
            put(COL_REVIEW_DATE, fmt.format(Date()))
        }
        val rows = writableDatabase.update(
            TABLE_REVIEWS,
            cv,
            "$COL_REVIEW_ID = ? AND $COL_REVIEW_USER_ID = ?",
            arrayOf(review.id.toString(), review.userId.toString())
        )
        return rows > 0
    }

    // Delete a review (only by its owner)
    fun deleteReview(reviewId: Int, userId: Int): Boolean {
        val rows = writableDatabase.delete(
            TABLE_REVIEWS,
            "$COL_REVIEW_ID = ? AND $COL_REVIEW_USER_ID = ?",
            arrayOf(reviewId.toString(), userId.toString())
        )
        return rows > 0
    }

    fun getReviewsForBook(bookId: Int): List<Review> {
        val list = mutableListOf<Review>()
        val cursor = readableDatabase.rawQuery(
            "SELECT r.*, u.$COL_USER_NAME FROM $TABLE_REVIEWS r " +
            "JOIN $TABLE_USERS u ON r.$COL_REVIEW_USER_ID = u.$COL_USER_ID " +
            "WHERE r.$COL_REVIEW_BOOK_ID = ? ORDER BY r.$COL_REVIEW_ID DESC",
            arrayOf(bookId.toString())
        )
        while (cursor.moveToNext()) {
            list.add(Review(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_REVIEW_ID)),
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_REVIEW_USER_ID)),
                bookId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_REVIEW_BOOK_ID)),
                userName = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAME)) ?: "",
                rating = cursor.getFloat(cursor.getColumnIndexOrThrow(COL_REVIEW_RATING)),
                comment = cursor.getString(cursor.getColumnIndexOrThrow(COL_REVIEW_COMMENT)) ?: "",
                date = cursor.getString(cursor.getColumnIndexOrThrow(COL_REVIEW_DATE)) ?: ""
            ))
        }
        cursor.close()
        return list
    }

    fun getAverageRating(bookId: Int): Float {
        val cursor = readableDatabase.rawQuery(
            "SELECT AVG($COL_REVIEW_RATING) FROM $TABLE_REVIEWS WHERE $COL_REVIEW_BOOK_ID = ?",
            arrayOf(bookId.toString())
        )
        val avg = if (cursor.moveToFirst()) cursor.getFloat(0) else 0f
        cursor.close()
        return avg
    }

    fun hasUserReviewed(userId: Int, bookId: Int): Boolean {
        val cursor = readableDatabase.query(
            TABLE_REVIEWS, arrayOf(COL_REVIEW_ID),
            "$COL_REVIEW_USER_ID = ? AND $COL_REVIEW_BOOK_ID = ?",
            arrayOf(userId.toString(), bookId.toString()), null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }
    
    // ─── ADMIN REVIEW OPERATIONS ─────────────────────────────────────
    fun isAdmin(userId: Int): Boolean {
        val cursor = readableDatabase.query(
            TABLE_USERS, arrayOf(COL_USER_EMAIL),
            "$COL_USER_ID = ?", arrayOf(userId.toString()), null, null, null
        )
        val admin = if (cursor.moveToFirst()) {
            cursor.getString(0) == "admin@perpustakaan.com"
        } else false
        cursor.close()
        return admin
    }

    fun adminUpdateReview(reviewId: Int, rating: Float, comment: String): Boolean {
        val fmt = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        val cv = ContentValues().apply {
            put(COL_REVIEW_RATING, rating)
            put(COL_REVIEW_COMMENT, comment)
            put(COL_REVIEW_DATE, fmt.format(Date()))
        }
        val rows = writableDatabase.update(
            TABLE_REVIEWS,
            cv,
            "$COL_REVIEW_ID = ?",
            arrayOf(reviewId.toString())
        )
        return rows > 0
    }

    // Fix incorrect cover image for specific book titles (development utility)
    fun fixBookCover(title: String, correctCover: String) {
        writableDatabase.execSQL("UPDATE $TABLE_BOOKS SET $COL_BOOK_COVER_IMAGE = ? WHERE $COL_BOOK_TITLE = ?", arrayOf(correctCover, title))
    }

    fun adminDeleteReview(reviewId: Int): Boolean {
        val rows = writableDatabase.delete(
            TABLE_REVIEWS,
            "$COL_REVIEW_ID = ?",
            arrayOf(reviewId.toString())
        )
        return rows > 0
    }

    // Existing closing brace
    // Reset sample books data – useful during development to ensure cover images match titles
    fun resetSampleData() {
        writableDatabase.execSQL("DELETE FROM $TABLE_BOOKS")
        insertSampleData(writableDatabase)
    }
}