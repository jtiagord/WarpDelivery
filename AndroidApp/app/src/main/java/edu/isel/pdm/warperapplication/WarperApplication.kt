package edu.isel.pdm.warperapplication

import android.app.Application
import edu.isel.pdm.warperapplication.web.entities.Delivery
import edu.isel.pdm.warperapplication.web.entities.Warper


class WarperApplication : Application() {
    private val repository by lazy {
        AppRepository(this)
    }

    fun tryLogin(
        username: String, password: String,
        onSuccess: (Boolean) -> Unit, onFailure: () -> Unit
    ) {
        return repository.tryLogin(username, password, onSuccess, onFailure)
    }

    fun tryRegister(
        username: String, password: String, fName: String, lName: String,
        email: String, phone: String,
        onSuccess: (Boolean) -> Unit, onFailure: () -> Unit
    ) {
        return repository.tryRegister(
            username, password, fName, lName, email, phone, onSuccess,
            onFailure
        )
    }

    fun getDeliveries(
        username: String,
        onSuccess: (List<Delivery>) -> Unit,
        onFailure: () -> Unit
    ) {
        return repository.getDeliveries(username, onSuccess, onFailure)
    }

    fun getUserInfo(
        username: String,
        onSuccess: (Warper) -> Unit,
        onFailure: () -> Unit
    ) {
        return repository.getUserInfo(username, onSuccess, onFailure)
    }

    fun getCurrentUser() : String{
        return repository.getCurrentUser()
    }
}