package com.hybrid.projectarea.domain.repository.checklist

import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.domain.model.checkListVehicle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class VehicleRepository(private val apiService: ApiService)  {
    suspend fun postVehicleCheckList(vehicle: checkListVehicle):Result<Unit>{
        return withContext(Dispatchers.IO){
            try {
                val response = apiService.postStoreCheckListVehicle(vehicle)
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