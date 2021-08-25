package edu.isel.pdm.warperapplication.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import edu.isel.pdm.warperapplication.WarperApplication

class DeliveryInfoViewModel (app: Application) : AndroidViewModel(app) {

    private val app: WarperApplication by lazy {
        getApplication<WarperApplication>()
    }

    fun getDeliveryInfo(){
        //TODO:
    }

}