package com.example.realestateapp.ui.houseList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.realestateapp.R
import com.example.realestateapp.data.model.House

/**
 * Adapter bound to the houses recycle view which handles the data and events initialization
 *
 * @property houses list of houses needed to populate the recycle view the adapter is bound to
 * @property onClick on house click event passed with the method from the house fragment
 */
class HouseAdapter(private val houses: ArrayList<House>, private val onClick: (House) -> Unit) :
    RecyclerView.Adapter<HouseAdapter.ViewHolder>() {

    class ViewHolder(view: View, val onClick: (House) -> Unit) : RecyclerView.ViewHolder(view) {
        private var currentHouse: House? = null
        val tvPrice: TextView = view.findViewById(R.id.housePrice)
        val tvAddress: TextView = view.findViewById(R.id.houseAddress)
        val tvBedrooms: TextView = view.findViewById(R.id.houseBedrooms)
        val tvBathrooms: TextView = view.findViewById(R.id.houseBathrooms)
        val tvLocation: TextView = view.findViewById(R.id.houseLocation)
        val tvSize: TextView = view.findViewById(R.id.houseSize)

        init {
            view.setOnClickListener {
                currentHouse?.let {
                    onClick(it)
                }
            }
        }

        fun setHouse(house: House) {
            currentHouse = house
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.house_row_item, parent, false)
        return ViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setHouse(houses[position])

        val priceText = "$" + String.format("%,d", houses[position].price)
        val addressText = houses[position].zip + " " + houses[position].city

        holder.tvPrice.text = priceText
        holder.tvAddress.text = addressText
        holder.tvBedrooms.text = houses[position].bedrooms.toString()
        holder.tvBathrooms.text = houses[position].bathrooms.toString()
        holder.tvSize.text = houses[position].size.toString()
        holder.tvLocation.text = houses[position].size.toString()

        if (houses[position].distance != -1f) {
            val distanceText = String.format("%.1f", houses[position].distance * 0.001) + " km"
            holder.tvLocation.text = distanceText
        } else {
            "unav".also { holder.tvLocation.text = it }
        }
    }

    override fun getItemCount() = houses.size
}