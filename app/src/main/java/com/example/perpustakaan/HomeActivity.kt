package com.example.perpustakaan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.perpustakaan.databinding.ActivityHomeBinding
import com.example.perpustakaan.fragment.FavoriteFragment
import com.example.perpustakaan.fragment.HomeFragment
import com.example.perpustakaan.fragment.MembersFragment
import com.example.perpustakaan.fragment.MyBooksFragment
import com.example.perpustakaan.utils.NotificationHelper
import com.example.perpustakaan.fragment.ProfileFragment
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import com.example.perpustakaan.worker.DueDateWorker

class HomeActivity : AppCompatActivity() {

    lateinit var binding: ActivityHomeBinding
    private var isAdmin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Create notification channel for due date alerts
        NotificationHelper.createChannel(this)
        // Schedule daily work for due date notifications
        scheduleDueDateWork()
        val prefs = getSharedPreferences("PerpustakaanApp", Context.MODE_PRIVATE)
        val email = prefs.getString("user_email", "") ?: ""
        isAdmin = email == "admin@perpustakaan.com"

        setupBottomNavigation()

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
    }

    private fun setupBottomNavigation() {
        if (isAdmin) {
            // Change the search menu item to Anggota for Admin
            val searchMenuItem = binding.bottomNavigation.menu.findItem(R.id.nav_search)
            searchMenuItem?.title = "Anggota"
            searchMenuItem?.setIcon(R.drawable.ic_people)
        } else {
            // Remove the search menu item entirely for regular Members
            binding.bottomNavigation.menu.removeItem(R.id.nav_search)
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { loadFragment(HomeFragment()); true }
                R.id.nav_search -> {
                    if (isAdmin) {
                        loadFragment(MembersFragment())
                        true
                    } else {
                        false
                    }
                }
                R.id.nav_favorites -> { loadFragment(FavoriteFragment()); true }
                R.id.nav_my_books -> { loadFragment(MyBooksFragment()); true }
                R.id.nav_profile -> { loadFragment(ProfileFragment()); true }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    fun switchToSearch() {
        // Open the dedicated SearchActivity instead of switching tab
        startActivity(Intent(this, SearchActivity::class.java))
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.bottomNavigation.selectedItemId != R.id.nav_home) {
            binding.bottomNavigation.selectedItemId = R.id.nav_home
        } else {
            @Suppress("DEPRECATION")
            super.onBackPressed()
        }
    }

    // Schedule periodic work for due date notifications
    private fun scheduleDueDateWork() {
        val workRequest = PeriodicWorkRequestBuilder<DueDateWorker>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DueDateWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
