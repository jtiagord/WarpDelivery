package edu.isel.pdm.warperapplication.viewModels


import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import edu.isel.pdm.warperapplication.WarperApplication
import edu.isel.pdm.warperapplication.web.entities.LocationEntity
import org.osmdroid.util.GeoPoint

enum class WarperState {
    INACTIVE, LOOKING_FOR_DELIVERY, RETRIEVING, DELIVERING
}

class LocationViewModel(app: Application) : AndroidViewModel(app) {
    var currentLocation = MutableLiveData<LocationEntity>()
    var pickupLocation = MutableLiveData<GeoPoint>()
    var deliveryLocation = MutableLiveData<GeoPoint>()
    var state = MutableLiveData(WarperState.INACTIVE)
    var vehicleIds = MutableLiveData<Array<String>>()
    var atDeliveryPoint = MutableLiveData(false)
    var atPickupPoint = MutableLiveData(false)


    private val app: WarperApplication by lazy {
        getApplication<WarperApplication>()
    }

    fun initFirestore() {
        app.initFirestore(
            onActiveWarper = {
                if(it.isNotEmpty() && (state.value == WarperState.INACTIVE || state.value == null)){
                    Log.d("WARPER", "SETTING ACTIVE")
                    state.postValue(WarperState.LOOKING_FOR_DELIVERY)
                }
            },
            onDeliveringWarper = {
                Log.d("DATA", it.toString())
                Log.d("ACTIVE", state.value.toString())
                if(it.isNotEmpty()){
                    Log.d("WARPER", "SETTING ACTIVE")

                    state.postValue(WarperState.valueOf(it["state"] as String))
                }

                updateDeliveryInfo(it)
            },
            onSubscriptionError = {
                Toast.makeText(getApplication(), "Couldn't subscribe to map updates", Toast.LENGTH_LONG)
                .show()
            }
        )
    }

    private fun updateDeliveryInfo(data: Map<String, Any>) {

        val delivery = data["delivery"] as Map<String, Any>
        val pickUpLoc = delivery["pickUpLocation"] as HashMap<String,Double>
        val deliveryLoc = delivery["deliveryLocation"] as HashMap<String, Double>
        val currentLoc = data["location"] as HashMap<String, Double>
        pickupLocation.postValue(GeoPoint(pickUpLoc["latitude"]!!, pickUpLoc["longitude"]!!))
        currentLocation.postValue(LocationEntity(currentLoc["latitude"]!!, currentLoc["longitude"]!!))
        deliveryLocation.postValue(GeoPoint(deliveryLoc["latitude"]!!, deliveryLoc["longitude"]!!))
    }

    fun detachListener() {
        Log.d("FIRESTORE", "DETACHING")
        app.detachListener()
    }

    fun setActive(vehicle: String) {
        app.setActive(
            vehicle,
            currentLocation.value!!,
            onSuccess = {
                Log.d("ACTIVE", "SUCCESS")
                state.postValue(WarperState.LOOKING_FOR_DELIVERY)
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

    fun updateCurrentLocation(location: LocationEntity) {
        if(state.value != WarperState.INACTIVE) app.updateCurrentLocation(location)
        currentLocation.postValue(location)
    }

    fun setInactive(){
        app.setInactive(
            onSuccess = {
                state.postValue(WarperState.INACTIVE)
            },
            onFailure = {
                Toast.makeText(getApplication(), "Failed to set as inactive", Toast.LENGTH_LONG).show()
            }
        )
    }

    fun confirmDelivery(){
        app.confirmDelivery(
            onSuccess = {
                state.postValue(WarperState.INACTIVE)
            },
            onFailure = {
                Toast.makeText(getApplication(), "Failed to confirm current delivery", Toast.LENGTH_LONG).show()
            }
        )
    }

}



