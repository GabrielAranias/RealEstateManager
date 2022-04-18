package com.openclassrooms.realestatemanager.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.utils.Converters

@Database(entities = [Estate::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class EstateDatabase : RoomDatabase() {

    abstract fun estateDao(): EstateDao

    companion object {
        @Volatile
        private var INSTANCE: EstateDatabase? = null

        fun getDatabase(context: Context): EstateDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EstateDatabase::class.java,
                    "estate_db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}