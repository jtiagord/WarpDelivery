package edu.isel.pdm.warperapplication.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.isel.pdm.warperapplication.R
import edu.isel.pdm.warperapplication.web.entities.Vehicle

class VehiclesAdapter(private val vehicles: List<Vehicle>, private val removeVehicle: (String) -> Unit) :
    RecyclerView.Adapter<VehiclesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehiclesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.vehicle_item, parent, false)
        view.setOnLongClickListener{
            val registration = it.findViewById<TextView>(R.id.registration)
            removeVehicle(registration.text.toString())
            return@setOnLongClickListener true
        }
        return VehiclesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return vehicles.size
    }

    override fun onBindViewHolder(holder: VehiclesViewHolder, position: Int) {
        return holder.bind(vehicles[position])
    }
}

class VehiclesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val registration: TextView = itemView.findViewById(R.id.registration)
    private val type: TextView = itemView.findViewById(R.id.type)

    fun bind(vehicle: Vehicle) {
        registration.text = itemView.context.getString(R.string.vehicle_registration, vehicle.registration)
        type.text = itemView.context.getString(R.string.vehicle_type, vehicle.type)
    }

}