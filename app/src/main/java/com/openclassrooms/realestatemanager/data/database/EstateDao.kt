package com.openclassrooms.realestatemanager.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openclassrooms.realestatemanager.data.model.Estate

@Dao
interface EstateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addEstate(estate: Estate)

    @Query("SELECT * FROM estate ORDER BY id ASC")
    fun readAllData(): LiveData<List<Estate>>
}