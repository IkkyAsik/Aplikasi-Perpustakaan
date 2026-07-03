package com.example.perpustakaan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.perpustakaan.databinding.ActivityHomeBinding
import com.example.perpustakaan.fragment.HomeFragment
import com.example.perpustakaan.fragment.MyBooksFragment
import com.example.perpustakaan.fragment.ProfileFragment
import com.example.perpustakaan.fragment.SearchFragment

class HomeActivity : AppCompatActivity() {

    lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { loadFragment(HomeFragment()); true }
                R.id.nav_search -> { loadFragment(SearchFragment()); true }
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
        binding.bottomNavigation.selectedItemId = R.id.nav_search
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
}
