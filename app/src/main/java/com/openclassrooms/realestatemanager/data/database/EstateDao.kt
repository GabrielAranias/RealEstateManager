package com.openclassrooms.realestatemanager.data.database

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.*
import com.openclassrooms.realestatemanager.data.model.Estate

@Dao
interface EstateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addEstate(estate: Estate)

    @Update
    suspend fun updateEstate(estate: Estate)

    @Query("SELECT * FROM estate ORDER BY id ASC")
    fun readAllData(): LiveData<List<Estate>>

    // --- FOR CONTENT PROVIDER ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addEstateForContentProvider(estate: Estate): Long

    @Update
    fun updateEstateForContentProvider(estate: Estate): Int

    @Query("SELECT * FROM estate ORDER BY id ASC")
    fun readAllDataWithCursor(): Cursor

    // --- FOR TESTING ---
    @Query("SELECT * FROM estate WHERE type LIKE :search")
    fun findEstateByType(search: String): LiveData<List<Estate>>
}