package edu.isel.pdm.warperapplication.web

import edu.isel.pdm.warperapplication.web.entities.*
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @GET("warpers/{username}/deliveries/")
    fun getWarperDeliveries(@Path("username") username: String): Call<List<Delivery>>

    @GET("warpers/{username}/")
    fun getWarperInfo(@Path("username") username: String): Call<Warper>

    @GET("warpers/vehicles")
    fun getWarperVehicles(@Header("Authorization") token: String): Call<List<Vehicle>>

    @POST("warpers/Login")
    fun tryLogin(@Body loginDetails: LoginDetails): Call<LoginToken>

    @POST("warpers/")
    fun tryRegister(@Body registerDetails: RegisterDetails): Call<Unit>

    @PUT("warpers/{username}/vehicles")
    fun tryAddVehicle(
        @Path("username") username: String,
        @Body vehicle: Vehicle,
        @Header("Authorization") token: String
    ): Call<Unit>

    @PUT("warpers/")
    fun updateWarper(@Body warper: WarperEdit, @Header("Authorization") token: String): Call<Unit>

    @POST("warpers/SetActive")
    fun setActive(
        @Body activeWarper: ActiveWarper,
        @Header("Authorization") token: String
    ): Call<Unit>

    @PUT("warpers/location")
    fun updateLocation(@Body location: Location, @Header("Authorization") token: String): Call<Unit>
}