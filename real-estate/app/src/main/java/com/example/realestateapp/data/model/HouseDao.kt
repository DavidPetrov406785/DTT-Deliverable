package com.example.realestateapp.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HouseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(houses: List<House>)

    @Query("SELECT * FROM house_table ORDER BY price ASC")
    suspend fun getAll(): List<House>

    @Query(
        "SELECT * " +
                "FROM house_table " +
                "WHERE zip LIKE '%' || :address || '%' " +
                "OR city LIKE '%' || :address || '%' " +
                "OR city || ' ' || zip LIKE '%' || :address || '%' " +
                "OR zip || ' ' || city LIKE '%' || :address || '%' " +
                "ORDER BY price"
    )
    suspend fun getByAddress(address: String): List<House>
}