package com.example.realestateapp.utils.interfaces

import androidx.lifecycle.LiveData
import com.example.realestateapp.data.model.House
import com.example.realestateapp.utils.Resource

interface HouseContract {

    interface Model {
        suspend fun fetchHousesFromAPI(): List<House>
        suspend fun insertHousesIntoDb(houses: List<House>)
        suspend fun getHousesFromDb(): List<House>
        suspend fun getHousesFromDbByAddress(address: String): List<House>
    }

    interface Presenter {
        fun fetchHousesFromDb(withLocation: Boolean)
        fun getHouses(): LiveData<Resource<List<House>>>
        fun searchByAddress(address: String)
    }

    interface View
}