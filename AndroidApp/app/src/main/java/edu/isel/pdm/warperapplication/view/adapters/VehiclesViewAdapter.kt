package edu.isel.pdm.warperapplication.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.isel.pdm.warperapplication.R
import edu.isel.pdm.warperapplication.web.entities.Vehicle

class VehiclesAdapter(private val vehicles: List<Vehicle>) :
    RecyclerView.Adapter<VehiclesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehiclesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.vehicle_item, parent, false)
        return VehiclesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return vehicles.size
    }

    override fun onBindViewHolder(holder: VehiclesViewHolder, position: Int) {
        return holder.bind(vehicles[position])
    }
}

//TODO: Improve aspect
class VehiclesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val registration: TextView = itemView.findViewById(R.id.registration)
    private val type: TextView = itemView.findViewById(R.id.type)

    fun bind(vehicle: Vehicle) {
        registration.text = "Registration: " + vehicle.registration
        type.text = "Type: " + vehicle.type
    }



}