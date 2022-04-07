package com.openclassrooms.realestatemanager.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "estate")
data class Estate(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val type: String,
    val district: String,
    val price: Int
) : Parcelable