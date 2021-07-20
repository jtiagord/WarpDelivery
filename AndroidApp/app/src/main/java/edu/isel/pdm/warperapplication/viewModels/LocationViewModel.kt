package edu.isel.pdm.warperapplication.viewModels


import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.isel.pdm.warperapplication.WarperApplication
import org.osmdroid.util.GeoPoint


class LocationViewModel(app: Application) : AndroidViewModel(app) {
    var startingLocation = MutableLiveData<GeoPoint>()
    var pickupLocation = MutableLiveData<GeoPoint>()
    var deliveryLocation = MutableLiveData<GeoPoint>()

    private val app: WarperApplication by lazy {
        getApplication<WarperApplication>()
    }

    fun initFirestore(){
        app.initFirestore(onStateChanged = {
            updateMapData(it)
        }, onSubscriptionError = {
            Toast.makeText(getApplication(), "Couldn't subscribe to map updates", Toast.LENGTH_LONG).show()
        })
    }

    private fun updateMapData(data: Map<String, Any>) {
        var delivery = data["deliveryLoc"] as ArrayList<Double>
        deliveryLocation.postValue(GeoPoint(delivery[0], delivery[1]))
        //var pickup = data["pickupLoc"] as Array<String>
        //var current = data["currentLoc"] as Array<String>

    }
}



