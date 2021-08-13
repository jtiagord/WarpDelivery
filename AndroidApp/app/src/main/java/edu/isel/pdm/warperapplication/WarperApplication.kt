package edu.isel.pdm.warperapplication

import android.app.Application
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import edu.isel.pdm.warperapplication.web.entities.*


class WarperApplication : Application() {
    private val repository by lazy {
        AppRepository(this)
    }

    fun initFirestore(
        onSubscriptionError: (Exception) -> Unit,
        onStateChanged: (Map<String, Any>) -> Unit
    )
    {
        repository.initFirestore(onSubscriptionError, onStateChanged)
    }

    fun tryLogin(
        username: String,
        password: String,
        onSuccess: (String?) -> Unit,
        onFailure: () -> Unit
    ) {
        return repository.tryLogin(username, password, onSuccess, onFailure)
    }

    fun tryRegister(
        username: String, password: String, fName: String, lName: String,
        email: String, phone: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: () -> Unit
    ) {
        return repository.tryRegister(
            username, password, fName, lName, email, phone, onSuccess, onFailure
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

    fun getVehicles(
        onSuccess: (List<Vehicle>) -> Unit,
        onFailure: () -> Unit
    ) {
        return repository.getVehicles(onSuccess, onFailure)
    }

    fun getCurrentUser(): String {
        return repository.getCurrentUser()
    }

    fun tryAddVehicle(
        username: String,
        vehicle: Vehicle,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        return repository.tryAddVehicle(username, vehicle, onSuccess, onFailure)
    }

    fun updateUser(
        user: WarperEdit,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        return repository.updateUser(user, onSuccess, onFailure)
    }

    fun setActive(
        vehicle: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("TOKEN", "Fetching registration token failed", task.exception)
                    return@OnCompleteListener
                }

                Log.d("TOKEN", task.result)

                // Get new FCM registration token
                //TODO: Get actual location
                val token = task.result
                repository.setActive(
                    vehicle, Location(38.74008721436314, -9.115295982914596),
                    token, onSuccess, onFailure
                )
            })
    }

    fun logout(){
        return repository.logout()
    }

    fun detachListener(){
        return repository.detachListeners()
    }

    fun updateCurrentLocation(location: Location) {
        return repository.updateCurrentLocation(location)
    }
}