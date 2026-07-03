package com.example.perpustakaan

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.perpustakaan.database.DatabaseHelper
import com.example.perpustakaan.databinding.ActivityRegisterBinding
import com.example.perpustakaan.model.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        binding.btnBack.setOnClickListener { finish() }

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, getString(R.string.error_email_invalid), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 6) {
                Toast.makeText(this, getString(R.string.error_password_short), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                Toast.makeText(this, getString(R.string.error_password_mismatch), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (db.isEmailExists(email)) {
                Toast.makeText(this, getString(R.string.error_email_exists), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dateStr = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())
            val user = User(name = name, email = email, password = password, phone = phone, createdAt = dateStr)
            val id = db.addUser(user)
            if (id > 0) {
                Toast.makeText(this, "Registrasi berhasil! Silakan login.", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Registrasi gagal, coba lagi.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvLogin.setOnClickListener { finish() }
    }
}
