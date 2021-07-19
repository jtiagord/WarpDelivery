package utils

import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

class Location (val latitude : Double, val longitude : Double){
    fun getDistance(otherLocation : Location) : Double{
        val lat1rad  = (PI /180)*latitude
        val lat2rad = (PI /180)*otherLocation.latitude
        val long1rad  = (PI /180)*longitude
        val long2rad =(PI /180)*otherLocation.longitude

        return acos(
            sin(lat1rad) * sin(lat2rad) +
                    cos(lat1rad) * cos(lat2rad) * cos(long1rad - long2rad)
        ) *6371000
    }
}

data class DeliveryMessage (
    val storeLocation : Location,
    val storeAddress : String,
    val storeId : String,
    val deliveryLocation: Location,
    val deliveryAddress : String,
    val deliverySize: String
)