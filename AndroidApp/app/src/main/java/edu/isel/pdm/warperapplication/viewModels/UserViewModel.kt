package edu.isel.pdm.warperapplication.viewModels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import edu.isel.pdm.warperapplication.R
import edu.isel.pdm.warperapplication.WarperApplication
import edu.isel.pdm.warperapplication.web.entities.Warper
import edu.isel.pdm.warperapplication.web.entities.WarperEdit

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

    fun logout() {
        app.logout()
    }

    fun updateUser(user: WarperEdit) {
        app.updateUser(user,
            onSuccess = {
                getUserInfo()
            },
            onFailure = {
                Toast.makeText(app, R.string.user_update_fail, Toast.LENGTH_LONG).show()
            }
        )
    }

}