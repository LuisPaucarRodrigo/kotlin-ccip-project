package com.hybrid.projectarea.domain.repository

import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.domain.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class ImageHistoryRepository(private val apiService: ApiService) {
    suspend fun getPhotos(token:String,code_id:String):Result<List<Photo>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.requestRegisterPhoto(token, code_id)
                if (response.isSuccessful) {
                    Result.success(response.body()!!)
                } else {
                    val error = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(error).getString("error")
                    } catch (e: Exception) {
                        "Ocurrio un error desconocido"
                    }
                    if (response.code() == 401 ) {
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