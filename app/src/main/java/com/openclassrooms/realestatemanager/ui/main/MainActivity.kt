package com.openclassrooms.realestatemanager.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpAppBarConfiguration()
    }

    // Set up toolbar to display custom labels x back btn in specified fragment
    private fun setUpAppBarConfiguration() {
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_fragment_container) as NavHostFragment? ?: return
        val navController = host.navController
        appBarConfiguration = AppBarConfiguration(setOf(R.id.addFragment))
        setupActionBarWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.main_fragment_container).navigateUp(appBarConfiguration)
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