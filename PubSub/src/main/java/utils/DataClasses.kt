
import utils.Size
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

data class ActiveWarper(val username : String, val location : Location, val deliverySize: Size, val token : String)

data class Delivery(val id : String, val size : Size, val pickUpLocation : Location, val deliveryLocation : Location)

class DeliveringWarper(val username : String, val location : Location, val deliveryId : String,
                       val delivery : Delivery){
    constructor(warper : ActiveWarper , delivery : Delivery)
            : this(warper.username, warper.location,delivery.id ,delivery)

}

class Location (val latitude : Double, val longitude : Double){
    fun getDistance(otherLocation : Location) : Double{
        val lat1rad  =(PI /180)*latitude
        val lat2rad  =(PI /180)*otherLocation.latitude
        val long1rad =(PI /180)*longitude
        val long2rad =(PI /180)*otherLocation.longitude

        return acos(
            sin(lat1rad) * sin(lat2rad) +
                    cos(lat1rad) * cos(lat2rad) * cos(long1rad - long2rad)
        ) *6371000
    }
}

