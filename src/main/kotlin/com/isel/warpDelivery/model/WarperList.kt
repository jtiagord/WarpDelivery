package com.isel.warpDelivery.model

import org.springframework.stereotype.Component



class WarperLocation(val username : String, val location : Location)


@Component
class WarperList(){
    private val warperLocationList : ArrayList<WarperLocation> = ArrayList()

    fun add(warperLocation : WarperLocation){
        warperLocationList.add(warperLocation)
    }

    fun getClosest(location : Location) : WarperLocation?{
        var closestWarperLocation : WarperLocation? = null
        var closestDistance = Double.POSITIVE_INFINITY
        for(warper in warperLocationList){
            val distance = warper.location.getDistance(location)
            if(distance < closestDistance) {
                closestWarperLocation = warper
                closestDistance = distance
            }
        }
        return closestWarperLocation
    }

}
