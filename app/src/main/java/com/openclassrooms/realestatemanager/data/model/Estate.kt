package com.openclassrooms.realestatemanager.data.model

import android.os.Parcelable
import androidx.room.Embedded
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
    val price: Int,
    val description: String,
    val surface: Int,
    val realtor: String,
    val status: String,
    @Embedded
    val rooms: Rooms,
    @Embedded
    val location: Location
) : Parcelable

@Parcelize
data class Rooms(
    val nbRooms: Int,
    val nbBedrooms: Int,
    val nbBathrooms: Int
) : Parcelable

@Parcelize
data class Location(
    val street: String,
    val city: String,
    val postalCode: Int,
    val country: String
) : Parcelable