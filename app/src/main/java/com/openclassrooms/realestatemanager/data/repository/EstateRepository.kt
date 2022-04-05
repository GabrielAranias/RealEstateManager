package com.openclassrooms.realestatemanager.data.repository

import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.data.database.EstateDao
import com.openclassrooms.realestatemanager.data.model.Estate

class EstateRepository(private val estateDao: EstateDao) {

    val readAllData: LiveData<List<Estate>> = estateDao.readAllData()

    suspend fun addEstate(estate: Estate) {
        estateDao.addEstate(estate)
    }
}