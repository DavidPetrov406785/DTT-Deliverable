package com.example.realestateapp.data.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [House::class], version = 1)
abstract class HouseDatabase : RoomDatabase() {

    abstract fun houseDAO(): HouseDao

    companion object {
        @Volatile
        private var INSTANCE: HouseDatabase? = null

        fun getDatabase(context: Context): HouseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HouseDatabase::class.java,
                    "house_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}