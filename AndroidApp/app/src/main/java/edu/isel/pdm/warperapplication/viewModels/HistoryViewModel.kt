package edu.isel.pdm.warperapplication.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.isel.pdm.warperapplication.web.ApiInterface
import edu.isel.pdm.warperapplication.web.entities.Delivery
import edu.isel.pdm.warperapplication.web.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryViewModel : ViewModel(){

    var deliveries = MutableLiveData<List<Delivery>>()

    private val request = ServiceBuilder.buildService(ApiInterface::class.java)
    private val call = request.getWarperDeliveries("user1")

    fun getDeliveries() {

        call.clone().enqueue(object : Callback<List<Delivery>> {
            override fun onResponse(call: Call<List<Delivery>>, response: Response<List<Delivery>>) {
                if (response.isSuccessful){
                    deliveries.postValue(response.body())
                }
            }
            override fun onFailure(call: Call<List<Delivery>>, t: Throwable) {
                Log.e("HISTORY", t.message!!)
            }
        })
    }

}