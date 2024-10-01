package com.hybrid.projectarea.domain.repository

import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.domain.model.ExpenseForm
import com.hybrid.projectarea.domain.model.ExpenseHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class ExpenseRepository(private val apiService: ApiService) {
    suspend fun getExpenses(token:String):Result<List<ExpenseHistory>>{
        return withContext(Dispatchers.IO){
            try {
                val response = apiService.expenseHistory(token)
                if (response.isSuccessful){
                    Result.success(response.body()!!)
                } else {
                    if (response.code() == 401){
                        Result.failure(Exception("Token Invalid"))
                    } else {
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

    suspend fun postExpenses(token: String,expense: ExpenseForm):Result<Unit>{
        return withContext(Dispatchers.IO){
            try {
                val response = apiService.expenseStore(token,expense)
                if (response.isSuccessful){
                    Result.success(Unit)
                }else {
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