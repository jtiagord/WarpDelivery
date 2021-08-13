package edu.isel.pdm.warperapplication.viewModels


import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import edu.isel.pdm.warperapplication.WarperApplication
import edu.isel.pdm.warperapplication.web.entities.Location
import org.osmdroid.util.GeoPoint


class LocationViewModel(app: Application) : AndroidViewModel(app) {
    var currentLocation = MutableLiveData<GeoPoint>()
    var pickupLocation = MutableLiveData<GeoPoint>()
    var deliveryLocation = MutableLiveData<GeoPoint>()
    var active = MutableLiveData<Boolean>()
    var vehicleIds = MutableLiveData<Array<String>>()


    private val app: WarperApplication by lazy {
        getApplication<WarperApplication>()
    }

    private val locationRequest = LocationRequest.create().apply {
        interval = 10000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val builder = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest)

    fun initFirestore() {
        app.initFirestore(
            onStateChanged = {
                if(it.isNotEmpty() && active.value == false)
                    Log.d("WARPER", "ACTIVEEEEEEEEE")
                    active.postValue(true)
                //updateMapData(it)
            },
            onSubscriptionError = {
                Toast.makeText(getApplication(), "Couldn't subscribe to map updates", Toast.LENGTH_LONG)
                .show()
            }
        )
    }

    private fun updateMapData(data: Map<String, Any>) {
        var delivery = data["deliveryLoc"] as ArrayList<Double>
        deliveryLocation.postValue(GeoPoint(delivery[0], delivery[1]))
        //var pickup = data["pickupLoc"] as Array<String>
        //var current = data["currentLoc"] as Array<String>

    }

    fun detachListener() {
        app.detachListener()
    }

    fun setActive(vehicle: String) {
        app.setActive(
            vehicle,
            onSuccess = {
                Log.d("ACTIVE", "SUCESS")
                active.postValue(true)
            },
            onFailure = {
                Toast.makeText(getApplication(), "Failed to set active", Toast.LENGTH_LONG)
                    .show()
            }
        )
    }

    fun getVehicles(){
        app.getVehicles(
            onSuccess = { vehicle ->
                vehicleIds.postValue(vehicle.map{it.registration}.toTypedArray())
        },
        onFailure = {
            Toast.makeText(getApplication(), "Failed get vehicles", Toast.LENGTH_LONG).show()
        })
    }

    fun updateCurrentLocation(latitude : Double, longitude: Double) {
        app.updateCurrentLocation(Location(latitude, longitude))
        currentLocation.postValue(GeoPoint(latitude, longitude))
    }

}



