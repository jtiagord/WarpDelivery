package edu.isel.pdm.warperapplication.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.isel.pdm.warperapplication.R
import edu.isel.pdm.warperapplication.web.entities.DeliveryFullInfo
import java.text.DateFormat.getDateTimeInstance
import java.util.*


class DeliveriesAdapter(private val deliveries: List<DeliveryFullInfo>) :
    RecyclerView.Adapter<DeliveriesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveriesViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.delivery_item, parent, false)



        return DeliveriesViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return deliveries.size
    }

    override fun onBindViewHolder(holder: DeliveriesViewHolder, position: Int) {
        return holder.bind(deliveries[position])
    }


}

class DeliveriesViewHolder(itemView: View, val context: Context) :
    RecyclerView.ViewHolder(itemView) {

    private val states = hashMapOf(
        Pair("LOOKING_FOR_WARPER", context.getString(R.string.state_looking)),
        Pair("DELIVERING", context.getString(R.string.state_delivering)),
        Pair("DELIVERED", context.getString(R.string.state_delivered)),
        Pair("CANCELLED", context.getString(R.string.state_cancelled))
    )

    private val types = hashMapOf(
        Pair("SMALL", context.getString(R.string.size_small)),
        Pair("MEDIUM", context.getString(R.string.size_medium)),
        Pair("LARGE", context.getString(R.string.size_large))
    )

    private val store: TextView = itemView.findViewById(R.id.store_name)
    private val storeAddress: TextView = itemView.findViewById(R.id.address)
    private val date: TextView = itemView.findViewById(R.id.delivery_date)

    private val type: TextView = itemView.findViewById(R.id.delivery_type)
    private val state: TextView = itemView.findViewById(R.id.delivery_state)
    private val clientPhone: TextView = itemView.findViewById(R.id.client_phone_item)

    fun bind(delivery: DeliveryFullInfo) {

        store.text = context.getString(R.string.delivery_item_store, delivery.store.name)
        date.text =
            context.getString(R.string.delivery_item_date, getDateTime(delivery.purchaseDate))
        storeAddress.text =
            context.getString(R.string.delivery_item_storeAddress, delivery.store.address)
        clientPhone.text =
            context.getString(R.string.delivery_item_clientPhone, delivery.clientPhone)

        type.text = context.getString(R.string.delivery_item_type, types[delivery.type])
        state.text = context.getString(R.string.delivery_item_state, states[delivery.state])
    }

    private fun getDateTime(timestamp: String?): String {

        if (timestamp == null)
            return "N/A"

        val dateFormat = getDateTimeInstance()
        val date = Date(timestamp.toLong())
        return dateFormat.format(date)
    }

}