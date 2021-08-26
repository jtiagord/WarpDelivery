package edu.isel.pdm.warperapplication.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import edu.isel.pdm.warperapplication.WarperApplication

class LoginViewModel(app: Application) : AndroidViewModel(app) {

    val loginStatus = MutableLiveData<Boolean>()
    var lastLogin : Pair<String,String>? = null

    private val app: WarperApplication by lazy {
        getApplication<WarperApplication>()
    }

    fun tryLogin(user: String, password: String) {
        lastLogin = Pair(user,password)
        app.tryLogin(user, password,
            onSuccess = {
                if(it != null){
                    loginStatus.postValue(true)
                }else {
                    loginStatus.postValue(false)
                }

            },
            onFailure = {
                loginStatus.postValue(false)
            })
    }
}