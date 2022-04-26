package com.example.realestateapp.data.api

import com.example.realestateapp.data.model.House
import retrofit2.http.GET

interface ApiInterface {

    @GET("house")
    suspend fun getAllHouses(): List<House>
}