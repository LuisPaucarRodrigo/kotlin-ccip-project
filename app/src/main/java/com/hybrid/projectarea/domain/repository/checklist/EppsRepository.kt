package com.hybrid.projectarea.domain.repository.checklist

import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.domain.model.checklistDiary
import com.hybrid.projectarea.domain.model.checklistEpps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class EppsRepository(private val apiService: ApiService) {
    suspend fun postEppsCheckList(epps: checklistEpps):Result<Unit>{
        return withContext(Dispatchers.IO){
            try {
                val response = apiService.postStoreCheckListEpps(epps)
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