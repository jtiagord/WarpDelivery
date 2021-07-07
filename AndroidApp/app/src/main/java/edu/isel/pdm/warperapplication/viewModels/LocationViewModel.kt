package edu.isel.pdm.warperapplication.viewModels


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.osmdroid.util.GeoPoint


class LocationViewModel : ViewModel() {
    var startingLoc : LiveData<GeoPoint> = TODO()
    var pickupLoc : LiveData<GeoPoint> = TODO()
    var deliveryLoc : LiveData<GeoPoint> = TODO()
}



