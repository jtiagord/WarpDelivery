package edu.isel.pdm.warperapplication.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.isel.pdm.warperapplication.R
import edu.isel.pdm.warperapplication.web.entities.Delivery

class DeliveriesAdapter(private val deliveries: List<Delivery>): RecyclerView.Adapter<DeliveriesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveriesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.delivery_item, parent, false)
        return DeliveriesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return deliveries.size
    }

    override fun onBindViewHolder(holder: DeliveriesViewHolder, position: Int) {
        return holder.bind(deliveries[position])
    }
}

//TODO: Improve aspect
class DeliveriesViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
    private val client: TextView = itemView.findViewById(R.id.client_username)
    private val deliveryDate: TextView = itemView.findViewById(R.id.delivery_date)
    private val rating: TextView = itemView.findViewById(R.id.delivery_rating)

    fun bind(delivery: Delivery) {

        client.text = "Client: " + delivery.clientUsername
        deliveryDate.text = "Delivery Date: " + delivery.deliverDate
        rating.text = "Rating: " + delivery.rating
    }

}