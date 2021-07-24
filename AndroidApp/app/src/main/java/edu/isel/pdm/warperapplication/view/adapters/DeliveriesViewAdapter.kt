package edu.isel.pdm.warperapplication.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.isel.pdm.warperapplication.R
import edu.isel.pdm.warperapplication.web.entities.Delivery
import java.text.DateFormat.getDateInstance
import java.text.DateFormat.getDateTimeInstance
import java.text.SimpleDateFormat
import java.util.*

class DeliveriesAdapter(private val deliveries: List<Delivery>): RecyclerView.Adapter<DeliveriesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveriesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.delivery_item, parent, false)
        return DeliveriesViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return deliveries.size
    }

    override fun onBindViewHolder(holder: DeliveriesViewHolder, position: Int) {
        return holder.bind(deliveries[position])
    }
}

class DeliveriesViewHolder(itemView : View, val context: Context): RecyclerView.ViewHolder(itemView){
    private val client: TextView = itemView.findViewById(R.id.client_username)
    private val date: TextView = itemView.findViewById(R.id.delivery_date)
    private val rating: TextView = itemView.findViewById(R.id.delivery_rating)
    private val reward: TextView = itemView.findViewById(R.id.delivery_reward)
    private val state: TextView = itemView.findViewById(R.id.delivery_state)

    fun bind(delivery: Delivery) {


        client.text = context.getString(R.string.delivery_item_client, delivery.clientUsername)
        date.text = context.getString(R.string.delivery_item_date, getDateTime(delivery.deliverDate))

        if(delivery.rating != null && delivery.reward != null ) {
            rating.text = context.getString(R.string.delivery_item_rating, delivery.rating)
            reward.text = context.getString(R.string.delivery_item_reward, delivery.reward)
        }

        state.text =context.getString(R.string.delivery_item_state, delivery.state)
    }

    private fun getDateTime(timestamp: String?) : String{

        if(timestamp == null)
            return "N/A"

        val dateFormat = getDateTimeInstance()
        val date = Date(timestamp.toLong())
        return dateFormat.format(date)
    }

}