package com.openclassrooms.realestatemanager.data.model

import android.content.ContentValues
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.openclassrooms.realestatemanager.utils.Constants
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "estate")
data class Estate(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var type: String,
    var district: String,
    var price: Int,
    var description: String,
    var surface: Int,
    var realtor: String,
    var status: String,
    var nbRooms: Int,
    var nbBedrooms: Int,
    var nbBathrooms: Int,
    var address: String,
    var entryDate: String,
    var saleDate: String,
    var vicinity: ArrayList<String>,
    var photoUris: ArrayList<String>,
    var photoCaptions: ArrayList<String>
) : Parcelable {

    constructor() : this(
        0,
        "",
        "",
        0,
        "",
        0,
        "",
        "",
        0,
        0,
        0,
        "",
        "",
        "",
        arrayListOf(""),
        arrayListOf(""),
        arrayListOf("")
    )

    // --- FOR CONTENT PROVIDER ---
    companion object {
        fun fromContentValues(
            values: ContentValues
        ): Estate {
            val estate = Estate()
            if (values.containsKey(Constants.TYPE)) estate.type =
                values.getAsString(Constants.TYPE)
            if (values.containsKey(Constants.DISTRICT)) estate.district =
                values.getAsString(Constants.DISTRICT)
            if (values.containsKey(Constants.PRICE)) estate.price =
                values.getAsInteger(Constants.PRICE)
            if (values.containsKey(Constants.DESCRIPTION)) estate.description =
                values.getAsString(Constants.DESCRIPTION)
            if (values.containsKey(Constants.SURFACE)) estate.surface =
                values.getAsInteger(Constants.SURFACE)
            if (values.containsKey(Constants.REALTOR)) estate.realtor =
                values.getAsString(Constants.REALTOR)
            if (values.containsKey(Constants.STATUS)) estate.status =
                values.getAsString(Constants.STATUS)
            if (values.containsKey(Constants.NB_ROOMS)) estate.nbRooms =
                values.getAsInteger(Constants.NB_ROOMS)
            if (values.containsKey(Constants.NB_BEDROOMS)) estate.nbBedrooms =
                values.getAsInteger(Constants.NB_BEDROOMS)
            if (values.containsKey(Constants.NB_BATHROOMS)) estate.nbBathrooms =
                values.getAsInteger(Constants.NB_BATHROOMS)
            if (values.containsKey(Constants.ADDRESS)) estate.address =
                values.getAsString(Constants.ADDRESS)
            if (values.containsKey(Constants.ENTRY_DATE)) estate.entryDate =
                values.getAsString(Constants.ENTRY_DATE)
            if (values.containsKey(Constants.SALE_DATE)) estate.saleDate =
                values.getAsString(Constants.SALE_DATE)
            if (values.containsKey(Constants.VICINITY)) estate.vicinity =
                arrayListOf(values.getAsString(Constants.VICINITY))
            if (values.containsKey(Constants.PHOTO_URIS)) estate.photoUris =
                arrayListOf(values.getAsString(Constants.PHOTO_URIS))
            if (values.containsKey(Constants.PHOTO_CAPTIONS)) estate.photoCaptions =
                arrayListOf(values.getAsString(Constants.PHOTO_CAPTIONS))
            return estate
        }
    }
}