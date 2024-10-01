package com.hybrid.projectarea.domain.repository

import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.domain.model.LoginRequest
import com.hybrid.projectarea.domain.model.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AuthRepository(private val apiService: ApiService) {
    suspend fun postAuth(login: LoginRequest):Result<LoginResponse>{
        return withContext(Dispatchers.IO){
            try {
                val response = apiService.login(login)
                if (response.isSuccessful){
                    Result.success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorBody).getString("error")
                    }catch (e:Exception){
                        "Ocurrio un error desconocido"
                    }
                    Result.failure(Exception(errorMessage))
                }
            }catch (e: Exception){
                Result.failure(e)
            }
        }
    }
}