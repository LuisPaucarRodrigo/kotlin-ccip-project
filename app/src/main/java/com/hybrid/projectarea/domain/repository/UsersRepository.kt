package com.hybrid.projectarea.domain.repository

import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.domain.model.UsersResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject


class UsersRepository(private val apiService: ApiService) {
    suspend fun fetchUsers(token:String,user_id:String): Result<UsersResponse> {
        return withContext(Dispatchers.IO){
            try {
                val response = apiService.users(token,user_id)
                if (response.isSuccessful){
                    Result.success(response.body()!!)
                }else{
                    if (response.code() == 401){
                        Result.failure(Exception("Token Invalid"))
                    }else{
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = try {
                            JSONObject(errorBody).getString("error")
                        }catch (e: JSONException){
                            "Ocurrio un Error Inesperado"
                        }
                        Result.failure(Exception("Error $errorMessage"))
                    }
                }
            }catch (e: Exception){
                Result.failure(e)
            }
        }
    }
}