package edu.isel.pdm.warperapplication.viewModels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import edu.isel.pdm.warperapplication.R
import edu.isel.pdm.warperapplication.WarperApplication
import edu.isel.pdm.warperapplication.web.entities.DeliveryFullInfo
import edu.isel.pdm.warperapplication.web.entities.LocationEntity
import org.osmdroid.util.GeoPoint
import java.lang.IllegalStateException

enum class WarperState {
    INACTIVE, LOOKING_FOR_DELIVERY, RETRIEVING, DELIVERING
}

class LocationViewModel(app: Application) : AndroidViewModel(app) {
    var currentLocation = MutableLiveData<LocationEntity>()
    var pickupLocation = MutableLiveData<GeoPoint>()
    var deliveryLocation = MutableLiveData<GeoPoint>()
    var deliveryId = MutableLiveData<String?>()
    var deliveryFullInfo = MutableLiveData<DeliveryFullInfo>()
    var state = MutableLiveData(WarperState.INACTIVE)
    var vehicleIds = MutableLiveData<Array<String>>()

    private val app: WarperApplication by lazy {
        getApplication<WarperApplication>()
    }

    fun initFirestore() {
        app.initFirestore(
            onActiveWarper = {
                if (it.isNotEmpty() && (state.value == WarperState.INACTIVE || state.value == null)) {
                    Log.d("STATE", "LOOKING FOR DELIVERY")
                    state.postValue(WarperState.LOOKING_FOR_DELIVERY)
                }
            },
            onDeliveringWarper = {
                if (it.isNotEmpty()) {
                    val status = it["state"] as String
                    state.postValue(WarperState.valueOf(status))
                    updateDeliveryInfo(it)
                }

            },
            onSubscriptionError = {
                Toast.makeText(getApplication(), R.string.firestore_updates_fail, Toast.LENGTH_LONG)
                    .show()
            }
        )
    }

    private fun updateDeliveryInfo(data: Map<String, Any>) {

        //Extract data from document
        val delivery = data["delivery"] as Map<String, Any>
        val pickupLoc = delivery["pickUpLocation"] as HashMap<String, Double>
        val deliveryLoc = delivery["deliveryLocation"] as HashMap<String, Double>
        val currLoc = data["location"] as HashMap<String, Double>
        val id = delivery["id"] as String

        //Update pickup, delivery and current location
        pickupLocation.postValue(GeoPoint(pickupLoc["latitude"]!!, pickupLoc["longitude"]!!))
        currentLocation.postValue(LocationEntity(currLoc["latitude"]!!, currLoc["longitude"]!!))
        deliveryLocation.postValue(GeoPoint(deliveryLoc["latitude"]!!, deliveryLoc["longitude"]!!))
        deliveryId.postValue(id)
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
                Log.d("STATE", "LOOKING FOR DELIVERY")
                state.postValue(WarperState.LOOKING_FOR_DELIVERY)
            },
            onFailure = {
                Toast.makeText(getApplication(), R.string.set_active_fail, Toast.LENGTH_LONG).show()
            }
        )
    }

    fun getVehicles() {
        app.getVehicles(
            onSuccess = { vehicle ->
                vehicleIds.postValue(vehicle.map { it.registration }.toTypedArray())
            },
            onFailure = {
                Toast.makeText(getApplication(), R.string.get_vehicles_fail, Toast.LENGTH_LONG)
                    .show()
            })
    }

    fun updateCurrentLocation(location: LocationEntity) {

        //Don't send location update API calls if the warper is inactive
        if (state.value != WarperState.INACTIVE)
            app.updateCurrentLocation(location)

        currentLocation.postValue(location)
    }

    fun setInactive() {
        app.setInactive(
            onSuccess = {
                Log.d("STATE", "INACTIVE")
                state.postValue(WarperState.INACTIVE)
            },
            onFailure = {
                Toast.makeText(getApplication(), R.string.set_inactive_fail, Toast.LENGTH_LONG)
                    .show()
            }
        )
    }

    fun confirmDelivery() {
        app.confirmDelivery(
            onSuccess = {
                Log.d("STATE", "INACTIVE")
                state.postValue(WarperState.INACTIVE)
            },
            onFailure = {
                Toast.makeText(getApplication(), R.string.delivery_confirm_fail, Toast.LENGTH_LONG)
                    .show()
            }
        )
    }

    fun revokeDelivery() {
        app.revokeDelivery(
            onSuccess = {
                Log.d("STATE", "INACTIVE")
                state.postValue(WarperState.INACTIVE)
            },
            onFailure = {
                Toast.makeText(getApplication(), R.string.delivery_revoke_fail, Toast.LENGTH_LONG)
                    .show()
            }
        )
    }

    fun getDeliveryInfo( onSuccess: (DeliveryFullInfo) -> Unit ){
        val deliveryId= deliveryId.value?:throw IllegalStateException("No Delivery To Fetch")

        app.getDeliveryInfo(deliveryId = deliveryId,
            onSuccess = {
                onSuccess(it)
            },
            onFailure = {
                Toast.makeText(getApplication(), R.string.delivery_get_fail, Toast.LENGTH_LONG)
                    .show()
            })
    }
}