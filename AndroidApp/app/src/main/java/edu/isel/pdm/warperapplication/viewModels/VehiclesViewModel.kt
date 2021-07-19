package edu.isel.pdm.warperapplication.viewModels

import android.app.Application
import android.util.Log
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
        app.getVehicles(app.getCurrentUser(),
            onSuccess = {
                vehicles.postValue(it)
            },
            onFailure = {
                vehicles.postValue(null)
            }
        )
    }

    fun addVehicle(vehicle: Vehicle){
        Log.v("VEHICLE", "ADDING")
        app.tryAddVehicle(app.getCurrentUser(), vehicle,
            onSuccess = {
                getVehicles()
            },
            onFailure = {
                //TODO:
            }
        )
    }
}