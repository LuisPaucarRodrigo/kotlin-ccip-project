package com.hybrid.projectarea.domain.repository.preproject

import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.domain.model.PreProject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject

class PreProjectRepository(private val apiService: ApiService) {
    suspend fun preproject(id:String): Result<List<PreProject>>{
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.preproject(id)
                if (response.isSuccessful) {
                    Result.success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorBody).getString("error")
                    } catch (e: JSONException) {
                        "Ocurrió un error desconocido"
                    }
                    if (response.code() == 401) {
                        Result.failure(Exception("Token no válido: $errorMessage"))
                    } else {
                        Result.failure(Exception("Error: $errorMessage"))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}