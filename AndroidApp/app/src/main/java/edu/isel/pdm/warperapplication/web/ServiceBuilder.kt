package edu.isel.pdm.warperapplication.web

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {

    private const val BASE_URL = "http://192.168.1.8:8081/WarpDelivery/"

    private val client = OkHttpClient.Builder().addInterceptor(Interceptor { chain ->
        val token = chain.request().header("Authorization")
        if (token != null) {
            val request = chain.request().newBuilder()
                .removeHeader("Authorization")
                .addHeader("Authorization", "Bearer $token")
                .build()
            return@Interceptor chain.proceed(request)
        }
        return@Interceptor chain.proceed(chain.request())
    }).build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun<T> buildService(service: Class<T>): T{
        return retrofit.create(service)
    }
}