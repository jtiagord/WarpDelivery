package edu.isel.pdm.warperapplication.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import edu.isel.pdm.warperapplication.WarperApplication
import edu.isel.pdm.warperapplication.web.entities.Delivery
import edu.isel.pdm.warperapplication.web.entities.DeliveryFullInfo


class HistoryViewModel(app: Application) : AndroidViewModel(app){

    private val app: WarperApplication by lazy {
        getApplication<WarperApplication>()
    }

    var deliveries = MutableLiveData<List<DeliveryFullInfo>>()

    fun getDeliveries(){
        app.getDeliveries(app.getCurrentUser(),
            onSuccess = {
                deliveries.postValue(it)
            },
            onFailure = {
                deliveries.postValue(null)
            }
        )
    }
}