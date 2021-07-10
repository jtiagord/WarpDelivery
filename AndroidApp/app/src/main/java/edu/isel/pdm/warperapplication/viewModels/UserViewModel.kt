package edu.isel.pdm.warperapplication.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.isel.pdm.warperapplication.WarperApplication
import edu.isel.pdm.warperapplication.web.ApiInterface
import edu.isel.pdm.warperapplication.web.ServiceBuilder
import edu.isel.pdm.warperapplication.web.entities.Warper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel(app: Application) : AndroidViewModel(app) {

    private val app: WarperApplication by lazy {
        getApplication<WarperApplication>()
    }
    var userInfo = MutableLiveData<Warper>()

    fun getUserInfo() {
        app.getUserInfo(app.getCurrentUser(),
            onSuccess = {
                userInfo.postValue(it)
            },
            onFailure ={
                userInfo.postValue(null)
            })
    }

}