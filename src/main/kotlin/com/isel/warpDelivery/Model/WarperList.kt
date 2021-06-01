package com.isel.warpDelivery.model

import org.springframework.stereotype.Component


@Component
class WarperList(val activeWarpers : WarperList){
    private val warperList : ArrayList<Warper> = ArrayList()

    fun add(warper : Warper){
        warperList.add(warper)
    }

    fun getClosest(location : Location) : Warper?{
        var closestWarper : Warper? = null
        var closestDistance = Double.POSITIVE_INFINITY
        for(warper in warperList){
            val distance = warper.location.getDistance(location)
            if(distance < closestDistance) {
                closestWarper = warper
                closestDistance = distance
            }
        }
        return closestWarper
    }

}
