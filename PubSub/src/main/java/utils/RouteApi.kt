package utils

import Location
import com.fasterxml.jackson.databind.ObjectMapper
import java.net.HttpURLConnection
import java.net.URL

class RouteApi{

    companion object{
        private const val API_URL=
            "http://router.project-osrm.org/route/v1/driving/"
    }

    fun getRouteDistance(from : Location, to : Location) : Double?{
        val url = URL("$API_URL${from.longitude},${from.latitude};${to.longitude},${to.latitude}")
        val stb = StringBuilder()
        println("$API_URL${from.longitude},${from.latitude};${to.longitude},${to.latitude}")

        with(url.openConnection() as HttpURLConnection) {
            inputStream.bufferedReader().use {
                it.lines().forEach { line ->
                    stb.append(line)
                }
            }
        }
        return extractDistanceFromJsonString(stb.toString())
    }



    private fun extractDistanceFromJsonString(jsonString : String): Double? {
        val mapper = ObjectMapper()
        val node = mapper.readTree(jsonString)
        val code = node.get("code").asText()

        if(code.lowercase() != "ok") return null;

        val routeNode = node.get("routes").get(0)
        return  routeNode.get("distance").asDouble()
    }
}