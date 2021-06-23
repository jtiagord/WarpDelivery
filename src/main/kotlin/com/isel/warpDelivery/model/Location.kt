package com.isel.warpDelivery.model

import kotlin.math.*

class Location (val latitude : Double, val longitude : Double){
    fun getDistance(otherLocation : Location) : Double{
        val lat1rad  = (PI/180)*latitude
        val lat2rad = (PI/180)*otherLocation.latitude
        val long1rad  = (PI/180)*longitude
        val long2rad =(PI/180)*otherLocation.longitude

        return acos(
            sin(lat1rad) * sin(lat2rad) +
                    cos(lat1rad) * cos(lat2rad) * cos(long1rad - long2rad)
        )*6371000


    }
}