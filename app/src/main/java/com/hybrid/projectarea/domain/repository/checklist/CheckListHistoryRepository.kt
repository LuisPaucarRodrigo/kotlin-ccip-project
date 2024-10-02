package com.hybrid.projectarea.domain.repository.checklist

import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.domain.model.ChecklistHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject

class CheckListHistoryRepository(private val apiService: ApiService) {
    suspend fun getChecklist():Result<List<ChecklistHistory>>{
        return withContext(Dispatchers.IO){
            try {
                val response = apiService.checklistHistory()
                if (response.isSuccessful){
                    Result.success(response.body()!!)
                } else {
                    if (response.code() == 401){
                        Result.failure(Exception("Token Invalid"))
                    }else{
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = try {
                            JSONObject(errorBody).getString("error")
                        } catch (e: JSONException) {
                            "Ocurrió un error desconocido"
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