package edu.isel.pdm.warperapplication

import android.app.Application
import android.util.Log
import edu.isel.pdm.warperapplication.web.ApiInterface
import edu.isel.pdm.warperapplication.web.ServiceBuilder
import edu.isel.pdm.warperapplication.web.entities.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppRepository(val app: Application) {

    var username: String? = null
    var password: String? = null
    var token: String? = null

    private val request = ServiceBuilder.buildService(ApiInterface::class.java)

    fun getCurrentUser(): String{
        return username!!
    }

    //onSuccess boolean represents if login was successful in terms of username/pw combo
    //onFailure is called if the connection fails
    fun tryLogin(user: String, pw: String, onSuccess: (Boolean) -> Unit, onFailure: () -> Unit) {

        val call = request.tryLogin(LoginDetails(user, pw))
        call.clone().enqueue(object : Callback<LoginToken> {
            override fun onResponse(call: Call<LoginToken>, response: Response<LoginToken>) {
                if (response.isSuccessful) {
                    username = user
                    password = pw
                    token = response.body()!!.token
                    onSuccess(true)
                } else {
                    onSuccess(false)
                    Log.v("REPO", "Failed login")
                }
            }

            override fun onFailure(call: Call<LoginToken>, t: Throwable) {
                onFailure()
                Log.v("REPO", "Connection error")
            }
        })
    }

    //onSuccess boolean represents if register was successful with the given info
    //onFailure is called if the connection fails
    fun tryRegister(
        user: String, pw: String, fName: String, lName: String, email: String,
        phone: String, onSuccess: (Boolean) -> Unit, onFailure: () -> Unit
    ) {
        val call = request.tryRegister(RegisterDetails(user, pw, fName, lName, email, phone))
        call.clone().enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    username = user
                    password = pw
                    onSuccess(true)
                } else {
                    Log.v("REPO", response.errorBody()!!.string())
                    onSuccess(false)
                    //TODO: Return reason why it failed
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onFailure()
                Log.v("REPO", "Connection error")
            }
        })
    }

    fun getDeliveries(username: String, onSuccess: (List<Delivery>) -> Unit, onFailure: () -> Unit) {
        val call = request.getWarperDeliveries(username)
        call.clone().enqueue(object : Callback<List<Delivery>> {
            override fun onResponse(call: Call<List<Delivery>>, response: Response<List<Delivery>>) {
                if (response.isSuccessful){
                    onSuccess(response.body()!!)
                }
            }
            override fun onFailure(call: Call<List<Delivery>>, t: Throwable) {
                onFailure()
                Log.e("HISTORY", t.message!!)
            }
        })
    }

    fun getUserInfo(username: String, onSuccess: (Warper) -> Unit, onFailure: () -> Unit) {
        val call = request.getWarperInfo(username)
        call.clone().enqueue(object : Callback<Warper> {
            override fun onResponse(call: Call<Warper>, response: Response<Warper>) {
                if (response.isSuccessful){
                    onSuccess(response.body()!!)
                }
            }
            override fun onFailure(call: Call<Warper>, t: Throwable) {
                onFailure()
                Log.e("USER", t.message!!)
            }
        })
    }
}