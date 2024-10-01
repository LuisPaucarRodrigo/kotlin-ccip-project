package com.hybrid.projectarea.model

import com.hybrid.projectarea.api.ApiService
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://erm.ccip.conproco.ccip.com.pe/api/"
//    private const val BASE_URL = "http://192.168.25.19:8000/api/"

    private var apiService: ApiService? = null
    private var httpClientWithAuth: OkHttpClient? = null

    private val httpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Método para obtener el cliente con autenticación
    private fun getHttpClientWithAuth(token: String): OkHttpClient {
        if (httpClientWithAuth == null) {
            httpClientWithAuth = httpClient.newBuilder()
                .addInterceptor { chain ->
                    val original: Request = chain.request()
                    val requestBuilder: Request.Builder = original.newBuilder()
                        .header("Authorization", "Bearer $token")
                        .header("Accept", "application/json")
                    val request: Request = requestBuilder.build()
                    chain.proceed(request)
                }
                .build()
        }
        return httpClientWithAuth!!
    }

    fun getClient(authToken: String? = null): ApiService {
        val client = if (authToken != null) {
            getHttpClientWithAuth(authToken)
        } else {
            httpClient
        }

        if (apiService == null) {
            apiService = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }

        return apiService!!

    }

    fun deleteHttpClient(){
        httpClientWithAuth = null
    }
}
