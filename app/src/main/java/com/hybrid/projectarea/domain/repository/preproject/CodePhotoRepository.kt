package com.hybrid.projectarea.domain.repository.preproject

import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.domain.model.PreprojectTitle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject

class CodePhotoRepository(private val apiService: ApiService) {
    suspend fun codePhotoPreProject(token: String, id: String): Result<List<PreprojectTitle>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.codephotopreproject(token, id)
                if (response.isSuccessful) {
                    Result.success(response.body()!!)
                } else {
                    if (response.code() == 401) {
                        Result.failure(Exception("Token no válido"))
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = try {
                            JSONObject(errorBody).getString("error")
                        } catch (e: JSONException) {
                            "Ocurrió un error desconocido"
                        }
                        Result.failure(Exception("Error: $errorMessage"))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
