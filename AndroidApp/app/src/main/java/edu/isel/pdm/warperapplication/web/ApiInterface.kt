package edu.isel.pdm.warperapplication.web

import edu.isel.pdm.warperapplication.web.entities.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiInterface {


    @GET("warpers/{username}/deliveries/")
    fun getWarperDeliveries(@Path("username") username: String) : Call<List<Delivery>>

    @GET("warpers/{username}/")
    fun getWarperInfo(@Path("username") username: String) : Call<Warper>

    @POST("warpers/Login")
    fun tryLogin(@Body loginDetails: LoginDetails): Call<LoginToken>

    @POST("warpers/")
    fun tryRegister(@Body registerDetails: RegisterDetails): Call<Unit>

}