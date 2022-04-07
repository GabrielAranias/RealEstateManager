package com.openclassrooms.realestatemanager.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container)
        val navController = navHostFragment?.findNavController()
        setupActionBarWithNavController(navController!!)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container)
        val navController = navHostFragment?.findNavController()
        return navController!!.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toolbar_add -> {
                navigateToAddFragment()
                true
            }
            else -> false
        }
    }

    private fun navigateToAddFragment() {
        val pendingIntent = NavDeepLinkBuilder(this.applicationContext)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.addFragment)
            .createPendingIntent()
        pendingIntent.send()
    }
}