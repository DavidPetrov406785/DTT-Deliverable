package com.example.realestateapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "house_table")
data class House(
    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    val id: Int,

    @ColumnInfo(name = "image")
    @SerializedName("image")
    val imagePath: String,

    @ColumnInfo(name = "price")
    @SerializedName("price")
    val price: Int,

    @ColumnInfo(name = "bedrooms")
    @SerializedName("bedrooms")
    val bedrooms: Int,

    @ColumnInfo(name = "bathrooms")
    @SerializedName("bathrooms")
    val bathrooms: Int,

    @ColumnInfo(name = "size")
    @SerializedName("size")
    val size: Int,

    @ColumnInfo(name = "description")
    @SerializedName("description")
    val description: String,

    @ColumnInfo(name = "zip")
    @SerializedName("zip")
    val zip: String,

    @ColumnInfo(name = "city")
    @SerializedName("city")
    val city: String,

    @ColumnInfo(name = "latitude")
    @SerializedName("latitude")
    val lat: Int,

    @ColumnInfo(name = "longitude")
    @SerializedName("longitude")
    val lng: Int,

    @ColumnInfo(name = "createdDate")
    @SerializedName("createdDate")
    val createdDate: String,

    ) : Serializable {
    var distance: Float = -1f
}
