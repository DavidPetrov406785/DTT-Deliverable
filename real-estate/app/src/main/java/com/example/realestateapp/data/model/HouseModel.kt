package com.example.realestateapp.data.model

import android.app.Application
import com.example.realestateapp.data.api.ApiClient
import com.example.realestateapp.data.api.ApiInterface
import com.example.realestateapp.utils.interfaces.HouseContract

/**
 * Serves as the connection between the presenter and the data classes of the room database and retrofit api call
 *
 * @param application to pass the context to the HouseDao to get the database
 */
class HouseModel(application: Application) : HouseContract.Model {

    private val apiClient: ApiInterface = ApiClient.getClient().create(ApiInterface::class.java)
    private val houseDao: HouseDao =
        HouseDatabase.getDatabase(application.applicationContext).houseDAO()

    override suspend fun fetchHousesFromAPI(): List<House> {
        return apiClient.getAllHouses()
    }

    override suspend fun insertHousesIntoDb(houses: List<House>) {
        houseDao.insertAll(houses)
    }

    override suspend fun getHousesFromDb(): List<House> {
        return houseDao.getAll()
    }

    override suspend fun getHousesFromDbByAddress(address: String): List<House> {
        return houseDao.getByAddress(address)
    }
}