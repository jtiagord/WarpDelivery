package com.isel.warpDelivery.model

import org.springframework.stereotype.Component
import routeAPI.RouteApi


class WarperLocation(val username : String, var location : Location)

const val MAX_DISTANCE = 15000 //in meters

@Component
class WarperList(val api : RouteApi){

    private val warperLocationList : ArrayList<WarperLocation> = ArrayList()
    private val warperLocationMap : MutableMap<String,WarperLocation> = HashMap()

    /**
     * Adds a warper to the list of searchable warpers
     * Returns false if there is already a warper with the same username
     *          true if it's added
     * **/
    fun add(warperLocation : WarperLocation) : Boolean{
        if(warperLocationMap.containsKey(warperLocation.username)) return false

        warperLocationList.add(warperLocation)
        warperLocationMap[warperLocation.username] = warperLocation
        return true;
    }

    fun getClosest(location : Location) : WarperLocation?{
        var closestWarperLocation : WarperLocation? = null
        var closestDistance = Double.POSITIVE_INFINITY
        for(warper in warperLocationList){
            val distance = warper.location.getDistance(location)

            if(distance > MAX_DISTANCE) continue;

            val routeDistance : Double = api.getRouteDistance(location, warper.location) ?: continue

            if(routeDistance < closestDistance) {
                closestWarperLocation = warper
                closestDistance = distance
            }
        }
        return closestWarperLocation
    }


    fun updateLocation(username : String, location: Location){
        warperLocationMap[username]?.location = location
    }

}
