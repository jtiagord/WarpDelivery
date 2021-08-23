package edu.isel.pdm.warperapplication.viewModels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import edu.isel.pdm.warperapplication.WarperApplication
import edu.isel.pdm.warperapplication.web.entities.Vehicle

class VehiclesViewModel(app: Application) : AndroidViewModel(app) {

    private val app: WarperApplication by lazy {
        getApplication<WarperApplication>()
    }

    var vehicles = MutableLiveData<List<Vehicle>>()

    fun getVehicles(){
        app.getVehicles(
            onSuccess = {
                vehicles.postValue(it)
            },
            onFailure = {
                vehicles.postValue(null)
            }
        )
    }

    //TODO: Use placeholder strings
    fun addVehicle(vehicle: Vehicle){
        app.tryAddVehicle(vehicle,
            onSuccess = {
                getVehicles()
            },
            onFailure = {
                Toast.makeText(app, "Failed to add", Toast.LENGTH_LONG).show()
            }
        )
    }

    fun removeVehicle(registration: String){
        app.removeVehicle(registration,
            onSuccess = {
                getVehicles()
            },
            onFailure = {
                Toast.makeText(app, "Failed to remove", Toast.LENGTH_LONG).show()
            }
        )
    }
}