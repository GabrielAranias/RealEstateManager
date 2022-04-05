package com.openclassrooms.realestatemanager.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.data.database.EstateDatabase
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.data.repository.EstateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EstateViewModel(application: Application) : AndroidViewModel(application) {

    private val readAllData: LiveData<List<Estate>>
    private val repository: EstateRepository

    init {
        val estateDao = EstateDatabase.getDatabase(application).estateDao()
        repository = EstateRepository(estateDao)
        readAllData = repository.readAllData
    }

    fun addEstate(estate: Estate) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addEstate(estate)
        }
    }
}