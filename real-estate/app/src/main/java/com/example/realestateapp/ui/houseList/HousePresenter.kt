package com.example.realestateapp.ui.houseList

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.core.location.LocationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.realestateapp.data.model.House
import com.example.realestateapp.data.model.HouseModel
import com.example.realestateapp.utils.interfaces.HouseContract
import com.example.realestateapp.utils.Resource
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.*
import kotlin.coroutines.resumeWithException

/**
 * Handles the communication between the view and model
 * @property application needed to be passed to the model and the location service
 */
class HousePresenter(private val application: Application) : HouseContract.Presenter {

    private var model: HouseContract.Model = HouseModel(application)
    private val houses = MutableLiveData<Resource<List<House>>>()

    /**
     * Main method for getting the houses from the database all done in the coroutine scope so all the code is ran on a separate thread from the ui thread
     * to not cause the UI to freeze and then a try block is started incorporating the rest of the code inside it
     *
     * First creates local value housesList initialized with model method getHouses which get all houses in the database and
     * lm as a Location Manager used for getting the location
     *
     * Next checks if the houseList is empty meaning there are no previous houses saved in the database,
     * if empty then the model method fetchHouses is called to get them from the API then populate the houseList with them
     *
     * After that is an if to check the withLocation param and if the location on the users device is on, if they are both true
     * local method onLocationEnabledAndGranted is called with param the current housesList value
     *
     * else a loop to go through all the houses in the houseList is ran and sets the distance for each to -1 signaling that there was some problem
     * with the location and later in the UI that would be displayed properly
     *
     * Lastly the housesList inserted into the database though the model method insertHouses with param the housesList and then the presenters local variable with inserted with the
     * Resource.success and the housesList from the database through the model method getHouses
     *
     * At the end there is catch block which catches all exceptions and calls the postValue method of the presenters variable houses with
     * Resources.error and passes only the exception as a string to the message param
     *
     * @param withLocation boolean to signalise if the location permission has been granted or not
     */
    override fun fetchHousesFromDb(withLocation: Boolean) {
        runBlocking {
            launch {
                try {
                    val housesList = model.getHousesFromDb() as MutableList<House>
                    val lm =
                        application.getSystemService(Context.LOCATION_SERVICE) as LocationManager

                    if (housesList.isEmpty()) {
                        val housesFromApi = model.fetchHousesFromAPI()
                        for (house in housesFromApi) {
                            housesList.add(house)
                        }
                    }

                    if (withLocation && LocationManagerCompat.isLocationEnabled(lm)) {
                        onLocationEnabledAndGranted(housesList)
                    } else {
                        for (house in housesList) {
                            house.distance = -1f
                        }
                        model.insertHousesIntoDb(housesList)
                        houses.postValue(Resource.success(model.getHousesFromDb()))
                    }
                } catch (e: Exception) {
                    houses.postValue(Resource.error(e.toString()))
                }
            }
        }
    }


    /**
     * Starts a coroutine scope and inside it a try block in which a local value called housesByAddress is initialized with the model method
     * getHousesByAddress with the address param and then the presenters variable houses is called method postValue with Resource.success
     * and the value housesByAddress
     * If an exception occurs the catch block catches it and calls method postValue with Resource.error with message param the exception to string
     * @param address a string containing the city and/or zip for which the user wants to search houses with
     */
    override fun searchByAddress(address: String) {
        runBlocking {
            launch {
                try {
                    val housesByAddress = model.getHousesFromDbByAddress(address)
                    houses.postValue(Resource.success(housesByAddress))
                } catch (e: Exception) {
                    houses.postValue(Resource.error(e.toString()))
                }
            }
        }
    }

    override fun getHouses(): LiveData<Resource<List<House>>> {
        return houses
    }

    /**
     * Launches a GlobalScope in which local value locationService is initialized with the fused location provider client with param application
     * and local method FusedLocationProviderClient.awaitCurrentLocation with the request priority int
     *
     * A for loop looping through all houses in the param houseList
     * if the locationService not null each house distance is set to the locationService method distance and the house's latitude and longitude
     * if the locationService is null the distance of each house is set to -1f
     *
     * Lastly the housesList inserted into the database though the model method insertHouses with param the housesList and then the presenters local variable with inserted with the
     * Resource.success and the housesList from the database through the model method getHouses
     *
     * At the end there is catch block which catches all exceptions and calls the postValue method of the presenters variable houses with
     * Resources.error and passes only the exception as a string to the message param
     *
     * @param housesList mutable list of houses from the database
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun onLocationEnabledAndGranted(housesList: MutableList<House>) {
        GlobalScope.launch {
            try {
                val locationService = LocationServices
                    .getFusedLocationProviderClient(application)
                    .awaitCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY)

                for (house in housesList) {
                    if (locationService != null) {
                        house.distance =
                            locationService.distanceTo(Location(LocationManager.GPS_PROVIDER).apply {
                                latitude = house.lat.toDouble()
                                longitude = house.lng.toDouble()
                            })
                    } else {
                        house.distance = 1f
                    }
                }
                model.insertHousesIntoDb(housesList)
                houses.postValue(Resource.success(model.getHousesFromDb()))
            } catch (e: Exception) {
                houses.postValue(Resource.error(e.toString()))
            }
        }
    }

    /**
     * In the return a suspendCancellableCoroutine is started a value cts is initialized with the CancellableTokenSource method
     *
     * getCurrentLocation is called with the param priority and the cts's token which has a on success listener which returns the gotten location
     * and a on failure listener with returns an exception
     *
     * @param priority the locations priority
     * @return cancellableContinuation with the users location
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @SuppressLint("MissingPermission")
    suspend fun FusedLocationProviderClient.awaitCurrentLocation(priority: Int): Location? {
        return suspendCancellableCoroutine {
            val cts = CancellationTokenSource()

            getCurrentLocation(priority, cts.token)
                .addOnSuccessListener { location ->
                    it.resume(location) { cts.cancel() }
                }.addOnFailureListener { e ->
                    it.resumeWithException(e)
                }
            it.invokeOnCancellation {
                cts.cancel()
            }
        }
    }
}