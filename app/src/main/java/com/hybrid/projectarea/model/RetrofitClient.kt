package com.hybrid.projectarea.model

import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
//    private const val BASE_URL = "https://erm.ccip.conproco.ccip.com.pe/api/"
    private const val BASE_URL = "http://192.168.1.15:8000/api/"

    fun getClient(authToken: String? = null): Retrofit {
        val httpClient = OkHttpClient.Builder()
        if (authToken != null) {
            httpClient.addInterceptor { chain ->
                val original: Request = chain.request()
                val requestBuilder: Request.Builder = original.newBuilder()
                    .header("Authorization", "Bearer $authToken")
                val request: Request = requestBuilder.build()
                chain.proceed(request)
            }
        }

        val client = httpClient.build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
