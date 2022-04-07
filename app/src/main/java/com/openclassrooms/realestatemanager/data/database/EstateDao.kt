package com.openclassrooms.realestatemanager.data.database

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
}