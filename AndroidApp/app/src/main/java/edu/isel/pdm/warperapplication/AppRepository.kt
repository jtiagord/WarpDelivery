package edu.isel.pdm.warperapplication

import android.app.Application
import android.util.Base64
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import edu.isel.pdm.warperapplication.web.ApiInterface
import edu.isel.pdm.warperapplication.web.ServiceBuilder
import edu.isel.pdm.warperapplication.web.entities.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val WARPERS_DELIVERIES_COLLECTION = "DELIVERINGWARPERS"
private const val WARPERS_COLLECTION = "WARPERS"

class AppRepository(val app: Application) {

    private var username: String? = null
    private var password: String? = null
    private var token: String? = null


    private val request = ServiceBuilder.buildService(ApiInterface::class.java)
    private val firestore = Firebase.firestore

    private lateinit var deliveriesDocRef: DocumentReference
    private lateinit var warpersDocRef: DocumentReference
    private var deliveringListenerRegistration: ListenerRegistration? = null
    private var activeListenerRegistration: ListenerRegistration? = null

    fun initFirestore(
        onSubscriptionError: (Exception) -> Unit,
        onStateChanged: (Map<String, Any>) -> Unit
    ) {
        deliveriesDocRef = firestore.collection(WARPERS_DELIVERIES_COLLECTION).document(username!!)
        warpersDocRef = firestore.collection(WARPERS_COLLECTION).document(username!!)

        deliveringListenerRegistration = deliveriesDocRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                onSubscriptionError(e)
                Log.w("FIRESTORE", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                onStateChanged(snapshot.data!!)
                Log.d("FIRESTORE", "Delivering warper current data: ${snapshot.data}")
            } else {
                Log.d("FIRESTORE", "Delivering warper data: null")
            }
        }

        activeListenerRegistration = warpersDocRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                onSubscriptionError(e)
                Log.w("FIRESTORE", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                onStateChanged(snapshot.data!!)
                Log.d("FIRESTORE", "Active warper data: ${snapshot.data}")
            } else {
                Log.d("FIRESTORE", "Active warper data: null")
            }
        }
    }

    fun updateMapInfo(data: MutableMap<String, Any>) {
        val deliveryLoc = data["deliveryLoc"]
        val pickupLoc = data["pickupLoc"]
        val warperLoc = data["warperLoc"]
    }

    fun getCurrentUser(): String {
        return username!!
    }

    //onSuccess returns the login token if successful or null if the user/pw combo is invalid
    //onFailure is called if the connection fails
    fun tryLogin(user: String, pw: String, onSuccess: (String?) -> Unit, onFailure: () -> Unit) {

        val call = request.tryLogin(LoginDetails(user, pw))
        call.clone().enqueue(object : Callback<LoginToken> {
            override fun onResponse(call: Call<LoginToken>, response: Response<LoginToken>) {
                if (response.isSuccessful) {
                    username = user
                    password = pw
                    token = response.body()!!.token
                    onSuccess(token)
                } else {
                    onSuccess(null)
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

    fun getDeliveries(
        username: String,
        onSuccess: (List<Delivery>) -> Unit,
        onFailure: () -> Unit
    ) {

        if (!tokenValid()) {
            tryLogin(this.username!!, this.password!!,
                onSuccess = {
                    getDeliveries(username, onSuccess, onFailure)
                }, onFailure = {
                    throw java.lang.IllegalStateException("Error getting token")
                }
            )
        } else {

            val call = request.getWarperDeliveries(username)
            call.clone().enqueue(object : Callback<List<Delivery>> {
                override fun onResponse(
                    call: Call<List<Delivery>>,
                    response: Response<List<Delivery>>
                ) {
                    if (response.isSuccessful) {
                        onSuccess(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<List<Delivery>>, t: Throwable) {
                    onFailure()
                    Log.e("HISTORY", t.message!!)
                }
            })
        }
    }

    fun getUserInfo(username: String, onSuccess: (Warper) -> Unit, onFailure: () -> Unit) {


        val call = request.getWarperInfo(username)
        call.clone().enqueue(object : Callback<Warper> {
            override fun onResponse(call: Call<Warper>, response: Response<Warper>) {
                if (response.isSuccessful) {
                    onSuccess(response.body()!!)
                }
            }

            override fun onFailure(call: Call<Warper>, t: Throwable) {
                onFailure()
                Log.e("USER", t.message!!)
            }
        })
    }

    fun getVehicles(onSuccess: (List<Vehicle>) -> Unit, onFailure: () -> Unit) {

        if (!tokenValid()) {
            tryLogin(this.username!!, this.password!!,
                onSuccess = {
                    getVehicles( onSuccess, onFailure)
                }, onFailure = {
                    throw java.lang.IllegalStateException("Error getting token")
                }
            )
        } else {
            val call = request.getWarperVehicles(token!!)
            call.clone().enqueue(object : Callback<List<Vehicle>> {
                override fun onResponse(call: Call<List<Vehicle>>, response: Response<List<Vehicle>>) {
                    if (response.isSuccessful) {
                        onSuccess(response.body()!!)
                    } else {
                        Log.d("VEHICLE", response.code().toString())
                        onFailure()
                    }
                }

                override fun onFailure(call: Call<List<Vehicle>>, t: Throwable) {
                    onFailure()
                    Log.e("VEHICLE", t.message!!)
                }
            })
        }
    }


    fun tryAddVehicle(
        username: String,
        vehicle: Vehicle,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {

        if (!tokenValid()) {
            tryLogin(this.username!!, this.password!!,
                onSuccess = {
                    tryAddVehicle(username, vehicle, onSuccess, onFailure)
                }, onFailure = {
                    throw java.lang.IllegalStateException("Error getting token")
                }
            )
        } else {
            val call = request.tryAddVehicle(username, vehicle, token!!)
            call.clone().enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful) {
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

    private fun tokenValid() : Boolean {

        val tkn = token ?: throw IllegalStateException("Token shouldn't be null")
        val splitToken: List<String> = tkn.split(".")

        val payload = String(Base64.decode(splitToken[1], Base64.DEFAULT))
        Log.v("PAYLOAD", payload)

        val gson = Gson()
        val parsedPayload = gson.fromJson(payload, TokenPayload::class.java)

        val expTimestamp = parsedPayload.exp
        val currTimestamp = System.currentTimeMillis() / 1000

        return currTimestamp <= expTimestamp

    }

    fun updateUser(
        user: WarperEdit,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        if (!tokenValid()) {
            tryLogin(this.username!!, this.password!!,
                onSuccess = {
                    updateUser(user, onSuccess, onFailure)
                }, onFailure = {
                    throw java.lang.IllegalStateException("Error getting token")
                }
            )
        } else {
            val call = request.updateWarper(user, token!!)
            call.clone().enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful) {
                        onSuccess()
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    //TODO: Handle
                    onFailure()
                    Log.e("UPDATE", t.message!!)
                }
            })
        }

    }

    fun logout() {

        //Clear user data
        username = null
        password = null
        token = null

        //Remove firebase listeners
        detachListeners()

        Log.v("LOGOUT", "LOGGED OUT")
    }


    fun detachListeners(){
        deliveringListenerRegistration?.remove()
        activeListenerRegistration?.remove()
    }

    fun setActive(vehicle: String,
                  location: Location,
                  nToken: String,
                  onSuccess: () -> Unit,
                  onFailure: () -> Unit
    ){
        if (!tokenValid()) {
            tryLogin(this.username!!, this.password!!,
                onSuccess = {
                    setActive(vehicle, location, nToken, onSuccess, onFailure)
                }, onFailure = {
                    throw java.lang.IllegalStateException("Error getting token")
                }
            )
        } else {
            val call = request.setActive(ActiveWarper(vehicle, location, nToken), token!!)
            call.clone().enqueue(object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>
                ) {
                    if (response.isSuccessful) {
                        Log.d("ACTIVE", "SUCCESS")
                        onSuccess()
                    } else {
                        onFailure()
                    }
                }
                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    onFailure()
                    Log.e("ACTIVE", t.message!!)
                }
            })
        }
    }

    fun setInactive(){
        //TODO: Set warper as inactive
    }

    fun updateCurrentLocation(location: Location) {
        if (!tokenValid()) {
            tryLogin(this.username!!, this.password!!,
                onSuccess = {
                    updateCurrentLocation(location)
                }, onFailure = {
                    throw java.lang.IllegalStateException("Error getting token")
                }
            )
        } else {
            val call = request.updateLocation(location, token!!)
            call.clone().enqueue(object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>
                ) {
                    if (response.isSuccessful) {
                        Log.d("LOCATION", "SUCCESS")
                    } else {
                        Log.d("LOCATION", "FAILED UPDATING LOCATION")
                    }
                }
                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.e("LOCATION", t.message!!)
                }
            })
        }
    }
}