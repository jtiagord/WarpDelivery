package edu.isel.pdm.warperapplication.viewModels


import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import edu.isel.pdm.warperapplication.WarperApplication
import edu.isel.pdm.warperapplication.web.entities.LocationEntity
import org.osmdroid.util.GeoPoint


class LocationViewModel(app: Application) : AndroidViewModel(app) {
    var currentLocation = MutableLiveData<LocationEntity>()
    var pickupLocation = MutableLiveData<GeoPoint>()
    var deliveryLocation = MutableLiveData<GeoPoint>()
    var active = MutableLiveData(false)
    var vehicleIds = MutableLiveData<Array<String>>()

    private val app: WarperApplication by lazy {
        getApplication<WarperApplication>()
    }

    fun initFirestore() {
        app.initFirestore(
            onActiveWarper = {
                if(it.isNotEmpty() && (active.value == false || active.value == null)){
                    Log.d("WARPER", "SETTING ACTIVE")
                    active.postValue(true)
                }
            },
            onDeliveringWarper = {
                Log.d("DATA", it.toString())
                Log.d("ACTIVE", active.value.toString())
                if(it.isNotEmpty() && (active.value == false || active.value == null)){
                    Log.d("WARPER", "SETTING ACTIVE")
                    active.postValue(true)
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

    fun updateCurrentLocation(location: LocationEntity) {
        app.updateCurrentLocation(location)
        currentLocation.postValue(location)
    }

    fun setInactive(){
        app.setInactive(
            onSuccess = {
                active.postValue(false)
            },
            onFailure = {
                Toast.makeText(getApplication(), "Failed to set as inactive", Toast.LENGTH_LONG).show()
            }
        )
    }

    fun finishDelivery(){
        app.finishDelivery(
            onSuccess = {
                active.postValue(false)
            },
            onFailure = {
                Toast.makeText(getApplication(), "Failed to set finish current delivery", Toast.LENGTH_LONG).show()
            }
        )
    }

}



