package routeAPI

import com.fasterxml.jackson.databind.ObjectMapper
import com.isel.warpDelivery.model.Location
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.net.HttpURLConnection
import java.net.URL

@Component
class RouteApi{

    val logger: Logger = LoggerFactory.getLogger(RouteApi::class.java)

    companion object{
        private const val API_URL=
            "http://router.project-osrm.org/route/v1/driving/"
    }


    //TODO HANDLE BAD REQUESTS
    fun getRouteDistance(from : Location, to : Location) : Double?{
        val url = URL("$API_URL${from.longitude},${from.latitude};${to.longitude},${to.latitude}")
        val stb = StringBuilder()

        with(url.openConnection() as HttpURLConnection) {

            logger.info("\nSent 'GET' request to URL : $url; Response Code : $responseCode")

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

        if(code.toLowerCase() != "ok") return null;

        val routeNode = node.get("routes").get(0)
        return  routeNode.get("distance").asDouble()
    }
}