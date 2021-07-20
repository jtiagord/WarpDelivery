package edu.isel.pdm.warperapplication

import android.app.Application
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.isel.pdm.warperapplication.web.ApiInterface
import edu.isel.pdm.warperapplication.web.ServiceBuilder
import edu.isel.pdm.warperapplication.web.entities.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val WARPERS_COLLECTION = "DELIVERINGWARPERS"

class AppRepository(val app: Application) {

    var username: String? = null
    var password: String? = null
    var token: String? = null

    private val request = ServiceBuilder.buildService(ApiInterface::class.java)
    private val firestore = Firebase.firestore

    lateinit var docRef: DocumentReference

    fun initFirestore(
        onSubscriptionError: (Exception) -> Unit,
        onStateChanged: (Map<String, Any>) -> Unit
    )
    {
        docRef = firestore.collection(WARPERS_COLLECTION).document(username!!)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                onSubscriptionError(e)
                Log.w("FIRESTORE", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                onStateChanged(snapshot.data!!)
                Log.d("FIRESTORE", "Current data: ${snapshot.data}")
            } else {
                Log.d("FIRESTORE", "Current data: null")
            }
        }
    }

    fun updateMapInfo(data: MutableMap<String, Any>) {
        val deliveryLoc = data["deliveryLoc"]
        val pickupLoc = data["pickupLoc"]
        val warperLoc = data["warperLoc"]
    }

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
                    Log.v("LOGIN", "Failed login")
                }
            }

            override fun onFailure(call: Call<LoginToken>, t: Throwable) {
                onFailure()
                Log.v("LOGIN", t.message!!)
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
                    Log.v("REGISTER", response.errorBody()!!.string())
                    onSuccess(false)
                    //TODO: Return reason why it failed
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onFailure()
                Log.v("REGISTER", t.message!!)
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

    fun getVehicles(username: String, onSuccess: (List<Vehicle>) -> Unit, onFailure: () -> Unit) {
        val call = request.getWarperVehicles(username)
        call.clone().enqueue(object : Callback<List<Vehicle>> {
            override fun onResponse(call: Call<List<Vehicle>>, response: Response<List<Vehicle>>) {
                if (response.isSuccessful){
                    onSuccess(response.body()!!)
                }
            }
            override fun onFailure(call: Call<List<Vehicle>>, t: Throwable) {
                onFailure()
                Log.e("VEHICLE", t.message!!)
            }
        })
    }

    fun tryAddVehicle(username: String, vehicle: Vehicle, onSuccess: () -> Unit, onFailure: () -> Unit){
        val call = request.tryAddVehicle(username, vehicle)
        call.clone().enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful){
                    onSuccess()
                }
            }
            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onFailure()
                Log.e("VEHICLE", t.message!!)
            }
        })
    }
}