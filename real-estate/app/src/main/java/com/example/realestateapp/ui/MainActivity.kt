package com.example.realestateapp.ui

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.realestateapp.R
import com.example.realestateapp.ui.Information.InformationFragment
import com.example.realestateapp.ui.houseList.HouseFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * The MainActivity class is responsible for showing the HouseFragment and InformationFragment in the frameLayout through
 * the bottom navigation and hiding the splash screen when the app is finished running background activities
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeTab -> {
                    loadOverview()
                }
                R.id.informationTab -> {
                    loadInformation()
                }
            }
            true
        }
        loadOverview()
    }

    /**
     * Creates the HouseFragment class and makes sure that the fragmentManager doesn't destroy it when the user switches between fragments
     * but hides it or and then shows it again and hides the InformationFragment
     */
    private fun loadOverview() {
        if (supportFragmentManager.findFragmentByTag(HouseFragment::class.java.simpleName) == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.frameLayout, HouseFragment(), HouseFragment::class.java.simpleName)
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .show(supportFragmentManager.findFragmentByTag(HouseFragment::class.java.simpleName)!!)
                .commit()
        }
        if (supportFragmentManager.findFragmentByTag(InformationFragment::class.java.simpleName) != null) {
            supportFragmentManager.beginTransaction()
                .detach(supportFragmentManager.findFragmentByTag(InformationFragment::class.java.simpleName)!!)
                .commit()
        }
    }

    /**
     * Creates the InformationFragment class and makes sure that the fragmentManager doesn't destroy it when the user switches between fragments
     * but hides it or and then shows it again and hides the HouseFragment
     */
    private fun loadInformation() {
        if (supportFragmentManager.findFragmentByTag(InformationFragment::class.java.simpleName) == null) {
            supportFragmentManager.beginTransaction().add(
                R.id.frameLayout,
                InformationFragment(),
                InformationFragment::class.java.simpleName
            ).commit()
        } else {
            supportFragmentManager.beginTransaction()
                .attach(supportFragmentManager.findFragmentByTag(InformationFragment::class.java.simpleName)!!)
                .commit()
        }
        if (supportFragmentManager.findFragmentByTag(HouseFragment::class.java.simpleName) != null) {
            supportFragmentManager.beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(HouseFragment::class.java.simpleName)!!)
                .commit()
        }
    }

    /**
     * The method when called hides the splash screen and shows the bottom navigation and frame layout
     */
    fun hideLoadingScreen() {
        val splashLayout: ConstraintLayout = findViewById(R.id.splash_screen)
        val frameLayout: FrameLayout = findViewById(R.id.frameLayout)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        splashLayout.visibility = View.INVISIBLE
        frameLayout.visibility = View.VISIBLE
        bottomNavigationView.visibility = View.VISIBLE
    }
}