package edu.isel.pdm.warperapplication.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.isel.pdm.warperapplication.web.ApiInterface
import edu.isel.pdm.warperapplication.web.ServiceBuilder
import edu.isel.pdm.warperapplication.web.entities.Delivery
import edu.isel.pdm.warperapplication.web.entities.Warper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel : ViewModel() {
    var user = MutableLiveData<Warper>()

    private val request = ServiceBuilder.buildService(ApiInterface::class.java)
    private val call = request.getWarperInfo("user3")

    fun getUserInfo() {

        call.clone().enqueue(object : Callback<Warper> {
            override fun onResponse(call: Call<Warper>, response: Response<Warper>) {
                if (response.isSuccessful){
                    Log.v("USER", response.body().toString())
                    user.postValue(response.body())
                }
            }
            override fun onFailure(call: Call<Warper>, t: Throwable) {
                Log.e("USER", t.message!!)
            }
        })
    }
}