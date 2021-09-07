package edu.isel.pdm.warperapplication.web

import edu.isel.pdm.warperapplication.web.entities.*
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    //Deliveries
    @GET("warpers/{username}/deliveries/")
    fun getWarperDeliveries(@Path("username") username: String, @Header("Authorization") token: String): Call<List<DeliveryFullInfo>>

    @GET("deliveries/{deliveryId}")
    fun getDeliveryInfo(@Path("deliveryId") deliveryId: String): Call<DeliveryFullInfo?>

    @POST("warpers/confirmDelivery")
    fun confirmDelivery(
        @Header("Authorization") token: String
    ): Call<Unit>

    @POST("warpers/revokeDelivery")
    fun revokeDelivery(
        @Header("Authorization") token: String
    ): Call<Unit>

    //User
    @GET("warpers/{username}/")
    fun getWarperInfo(@Path("username") username: String): Call<Warper>

    @PUT("warpers/")
    fun updateWarper(@Body warper: WarperEdit, @Header("Authorization") token: String): Call<Unit>

    //Auth
    @POST("warpers/Login")
    fun tryLogin(@Body loginDetails: LoginDetails): Call<LoginToken>

    @POST("warpers/")
    fun tryRegister(@Body registerDetails: RegisterDetails): Call<Unit>

    //Vehicles
    @PUT("warpers/{username}/vehicles")
    fun tryAddVehicle(
        @Path("username") username: String,
        @Body vehicle: Vehicle,
        @Header("Authorization") token: String
    ): Call<Unit>

    @GET("warpers/vehicles")
    fun getWarperVehicles(@Header("Authorization") token: String): Call<List<Vehicle>>

    @DELETE("warpers/{username}/vehicles/{registration}")
    fun removeVehicle(
        @Path("username") username: String,
        @Path("registration") registration: String,
        @Header("Authorization") token: String
    ): Call<Unit>

    //Status
    @POST("warpers/SetActive")
    fun setActive(
        @Body activeWarper: ActiveWarper,
        @Header("Authorization") token: String
    ): Call<Unit>

    @PUT("warpers/SetInactive")
    fun setInactive(
        @Header("Authorization") token: String
    ): Call<Unit>

    //Location
    @PUT("warpers/location")
    fun updateLocation(
        @Body location: LocationEntity,
        @Header("Authorization") token: String
    ): Call<Unit>
}