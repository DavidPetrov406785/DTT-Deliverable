package com.example.realestateapp.ui.houseDetails

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.realestateapp.R
import com.example.realestateapp.data.model.House
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * The activity responsible for the setting all the ui elemets with the passed as a serializable extra clicked house
 */
class HouseDetailsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    var house: House? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.house_details_activity)

        house = intent.getSerializableExtra("clickedHouse") as House

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.houseMap) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        val ivBack: ImageView = findViewById(R.id.backButton)
        val tvPrice: TextView = findViewById(R.id.houseDetailsPrice)
        val tvDescription: TextView = findViewById(R.id.houseDetailsDescription)

        val tvBedrooms: TextView = findViewById(R.id.houseBedrooms)
        val tvBathrooms: TextView = findViewById(R.id.houseBathrooms)
        val tvSize: TextView = findViewById(R.id.houseSize)
        val tvLocation: TextView = findViewById(R.id.houseLocation)

        tvPrice.text = house!!.price.toString()
        tvBedrooms.text = house!!.bedrooms.toString()
        tvBathrooms.text = house!!.bathrooms.toString()
        tvSize.text = house!!.size.toString()
        tvDescription.text = house!!.description
        val priceText = "$" + String.format("%,d", house!!.price)
        tvPrice.text = priceText

        if (house!!.distance != -1f) {
            val distanceText = String.format("%.1f", house!!.distance * 0.001) + " km"
            tvLocation.text = distanceText
        } else {
            "unav".also { tvLocation.text = it }
        }

        ivBack.setOnClickListener { finish() }
    }

    /**
     * When the map is loaded creates a marker on the house's location and zooms in on it
     *
     * @param googleMap the map of the activity
     */
    override fun onMapReady(googleMap: GoogleMap) {
        val loc = LatLng(house!!.lat.toDouble(), house!!.lng.toDouble())
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 12f))
        googleMap.addMarker(
            MarkerOptions().position(loc)
        )
        googleMap.setOnMapClickListener(this)
    }

    /**
     * When the map is clicked starts the google maps navigation to the house's location
     *
     * @param p0
     */
    override fun onMapClick(p0: LatLng) {
        val gmmIntentUri = Uri.parse("google.navigation:q=" + house!!.lat + "," + house!!.lng)
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

}