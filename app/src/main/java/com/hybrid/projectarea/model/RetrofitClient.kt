package com.hybrid.projectarea.model

import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
//    private const val BASE_URL = "https://erm.ccip.conproco.ccip.com.pe/api/"
    private const val BASE_URL = "http://192.168.1.12:8000/api/"

    private val httpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }


    fun getClient(authToken: String? = null): Retrofit {
        val client = if (authToken != null) {
            httpClient.newBuilder()
                .addInterceptor { chain ->
                    val original: Request = chain.request()
                    val requestBuilder: Request.Builder = original.newBuilder()
                        .header("Authorization", "Bearer $authToken")
                        .header("Accept", "application/json")
                    val request: Request = requestBuilder.build()
                    chain.proceed(request)
                }
                .build()
        } else {
            httpClient
        }

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
