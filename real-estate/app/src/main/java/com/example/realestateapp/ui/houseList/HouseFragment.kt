package com.example.realestateapp.ui.houseList

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.realestateapp.R
import com.example.realestateapp.data.model.House
import com.example.realestateapp.utils.interfaces.HouseContract
import com.example.realestateapp.ui.MainActivity
import com.example.realestateapp.ui.houseDetails.HouseDetailsActivity
import com.example.realestateapp.utils.Status

/**
 * Handles the UI logic of the house fragment by getting data from the presenter as being part of the HouseContract as a View
 */
class HouseFragment : Fragment(), HouseContract.View {

    private lateinit var presenter: HouseContract.Presenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.house_fragment, container, false)
    }

    /**
     * When the view is created the presenter is created, location permission is asked to be granted, ui and observer logic are initialized
     * @param view
     * @param savedInstanceState
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = HousePresenter(requireActivity().application)
        requirePermission()
        setupUI()
        setupObserver()
    }

    /**
     * Sets up on click listener of the search icon to clear text in the text view on click and
     * the on text change event on the address input text view to change the search icon if the text is not empty and call the
     * presenter's searchByAddress method with the current text
     */
    private fun setupUI() {
        val addressInput: TextView = requireView().findViewById(R.id.searchInput)
        val searchIcon: ImageView = requireView().findViewById(R.id.searchIcon)

        searchIcon.setOnClickListener { addressInput.text = "" }
        addressInput.doOnTextChanged { text, _, _, _ ->
            if (text.toString() != "") {
                searchIcon.setImageResource(R.drawable.ic_close)
            } else {
                searchIcon.setImageResource(R.drawable.ic_search)
            }

            presenter.searchByAddress(text.toString())
        }
    }


    /**
     * Sets up the observer on the getHouses method of presenter performs action when the "status" of the observed object changes
     * On Status.SUCCESS enabled the addressInput and hide apiErrorLayout and then call the renderList method with the data received and
     * calls the hide loading screen of the MainActivity
     * On Status.ERROR disables the addressInput so the user doesn't interact with it if there is no data and shows the apiErrorLayout to
     * explain the user that there is an error and what to do and finally call the hideLoadingScreen method of the MainActivity
     */
    private fun setupObserver() {
        val apiErrorLayout: ConstraintLayout = requireView().findViewById(R.id.apiErrorLayout)
        val addressInput: TextView = requireView().findViewById(R.id.searchInput)

        presenter.getHouses().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    addressInput.isEnabled = true
                    apiErrorLayout.visibility = View.INVISIBLE
                    it.data?.let { houses -> renderList(houses) }
                    (activity as MainActivity).hideLoadingScreen()
                }
                Status.ERROR -> {
                    addressInput.isEnabled = false
                    apiErrorLayout.visibility = View.VISIBLE
                    (activity as MainActivity).hideLoadingScreen()
                    Log.e("Observer Error", it.message.toString())
                }
            }
        }
    }

    /**
     * Creates the house adapter with the houses parameter and house on click listener and sept up the recycle view with it
     * checks if the houses in the houseAdapter are 0 and shows the noResultLayout if true
     * @param houses list of house objects received from the presenter
     */
    private fun renderList(houses: List<House>) {
        val noResultLayout: ConstraintLayout = requireView().findViewById(R.id.noResultLayout)

        // adapter code
        val houseAdapter =
            HouseAdapter(houses as ArrayList<House>) { house -> adapterOnClick(house) }
        val recyclerView: RecyclerView = requireView().findViewById(R.id.housesRecyclerView)
        recyclerView.adapter = houseAdapter

        // no result code
        if (houseAdapter.itemCount == 0) {
            noResultLayout.visibility = View.VISIBLE
        } else {
            noResultLayout.visibility = View.INVISIBLE
        }
    }

    /**
     * Starts the HouseDetailsActivity with the clicked house
     * @param house the clicked house in the recycle view
     */
    private fun adapterOnClick(house: House) {
        val intent = Intent(requireContext(), HouseDetailsActivity()::class.java)
        intent.putExtra("clickedHouse", house)
        startActivity(intent)
    }

    /**
     * local variables for requesting for location access permission and getting the users input
     * If the permission is granted calls the presenter method fetchHouses with param true to showcase that the location permission was granted
     * If the user denies, local method startPermissionDialog is called
     * else presenter method fetchHouses is called with param false
     */
    private val permissionRequester =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            when {
                isGranted -> presenter.fetchHousesFromDb(true)
                ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) -> {
                    startPermissionDialog()
                }
                else -> {
                    presenter.fetchHousesFromDb(false)
                }
            }
        }

    /**
     * Starts dialog to explain why the app need location permission
     * if the user still dismisses fetchHouses is called with param false as the location permission was not granted
     * if the user allows method requirePermission is called to ask the user to grant permission again
     */
    private fun startPermissionDialog() {
        AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle)
            .setTitle(resources.getString(R.string.alert_title))
            .setMessage(resources.getString(R.string.alert_message))
            .setPositiveButton(resources.getString(R.string.alert_positive)) { _, _ ->
                requirePermission()
            }
            .setNegativeButton(resources.getString(R.string.alert_negative)) { _, _ ->
                presenter.fetchHousesFromDb(false)
            }
            .setCancelable(false)
            .create()
            .show()
    }

    /**
     * Calls the variable permissionRequester with fine location permission
     */
    private fun requirePermission() {
        permissionRequester.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}















