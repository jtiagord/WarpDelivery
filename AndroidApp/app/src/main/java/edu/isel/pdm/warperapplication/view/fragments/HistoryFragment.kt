package edu.isel.pdm.warperapplication.view.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.isel.pdm.warperapplication.R
import edu.isel.pdm.warperapplication.view.DeliveriesAdapter
import edu.isel.pdm.warperapplication.web.ApiInterface
import edu.isel.pdm.warperapplication.web.Delivery
import edu.isel.pdm.warperapplication.web.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_history, container, false)
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerView)

        val request = ServiceBuilder.buildService(ApiInterface::class.java)
        val call = request.getWarperDeliveries("user1")


        call.enqueue(object : Callback<List<Delivery>> {
            override fun onResponse(call: Call<List<Delivery>>, response: Response<List<Delivery>>) {


                if (response.isSuccessful){
                    recyclerView.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(activity)
                        adapter = DeliveriesAdapter(response.body()!!)
                    }
                }
            }
            override fun onFailure(call: Call<List<Delivery>>, t: Throwable) {
                Log.v("HISTORY", t.message!!)
                throw t
                Toast.makeText(activity, "${t.message}", Toast.LENGTH_LONG).show()
            }
        })

        return rootView
    }

}