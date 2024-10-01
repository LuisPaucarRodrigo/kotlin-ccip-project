package com.hybrid.projectarea.domain.repository.preproject

import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.domain.model.CodePhotoDescription
import com.hybrid.projectarea.domain.model.ImageReport
import com.hybrid.projectarea.domain.model.checkListVehicle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class EnterImageRepository(private val apiService: ApiService) {
    suspend fun getData(token:String,code_id: String):Result<CodePhotoDescription>{
        return withContext(Dispatchers.IO){
            try {
                val response = apiService.codephotoespecific(token,code_id)
                if (response.isSuccessful){
                    Result.success(response.body()!!)
                }else{
                    if (response.code() == 401){
                        Result.failure(Exception("Token Invalid"))
                    }else{
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = try {
                            JSONObject(errorBody).getString("error")
                        }catch (e:Exception){
                            "Ocurrio un error desconocido"
                        }
                        Result.failure(Exception(errorMessage))
                    }
                }
            }catch (e:Exception){
                Result.failure(e)
            }
        }
    }

    suspend fun postImage(token:String,image: ImageReport):Result<Unit>{
        return withContext(Dispatchers.IO){
            try {
                val response = apiService.postImageReport(token,image)
                if (response.isSuccessful){
                    Result.success(Unit)
                }else{
                    if (response.code() == 401){
                        Result.failure(Exception("Token Invalid"))
                    }else{
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = try {
                            JSONObject(errorBody).getString("error")
                        }catch (e:Exception){
                            "Ocurrio un error desconocido"
                        }
                        Result.failure(Exception(errorMessage))
                    }
                }
            }catch (e:Exception){
                Result.failure(e)
            }
        }
    }
}