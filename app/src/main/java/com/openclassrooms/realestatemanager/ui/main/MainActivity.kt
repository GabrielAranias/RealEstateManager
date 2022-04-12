package com.openclassrooms.realestatemanager.ui.main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.utils.Constants

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container)
        val navController = navHostFragment?.findNavController()
        setupActionBarWithNavController(navController!!)

        createNotificationChannel()
    }

    // Create notification channel so that user receives notification after creating new estate
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_title)
            val descriptionText = getString(R.string.notification_channel_description)
            val channel = NotificationChannel(
                Constants.CHANNEL_ID,
                name,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
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