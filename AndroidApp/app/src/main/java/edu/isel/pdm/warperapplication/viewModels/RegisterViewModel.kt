package edu.isel.pdm.warperapplication.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import edu.isel.pdm.warperapplication.WarperApplication

class RegisterViewModel(app: Application) : AndroidViewModel(app) {

    val registerStatus = MutableLiveData<Boolean>()

    private val app: WarperApplication by lazy {
        getApplication<WarperApplication>()
    }

    fun tryRegister(
        user: String, password: String, fName: String, lName: String, email: String, phone: String
    ) {
        app.tryRegister(user, password, fName, lName, email, phone,
            onSuccess = {
                registerStatus.postValue(it)
            },
            onFailure = {
                registerStatus.postValue(false)
            })
    }
}