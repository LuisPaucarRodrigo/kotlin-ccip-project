package com.hybrid.projectarea.api

import com.hybrid.projectarea.domain.model.CodePhotoDescription
import com.hybrid.projectarea.domain.model.ElementPreProjectRecyclerView
import com.hybrid.projectarea.domain.model.Photo
import com.hybrid.projectarea.domain.model.PhotoRequest
import com.hybrid.projectarea.domain.model.PreprojectTitle
import com.hybrid.projectarea.domain.model.UsersResponse
import com.hybrid.projectarea.domain.model.ChecklistHistory
import com.hybrid.projectarea.domain.model.Download
import com.hybrid.projectarea.domain.model.ExpenseForm
import com.hybrid.projectarea.domain.model.ExpenseHistory
import com.hybrid.projectarea.domain.model.FolderArchiveResponse
import com.hybrid.projectarea.domain.model.FormProcessManuals
import com.hybrid.projectarea.domain.model.FormStoreProjectHuawei
import com.hybrid.projectarea.domain.model.LoginRequest
import com.hybrid.projectarea.domain.model.LoginResponse
import com.hybrid.projectarea.domain.model.ProjectHuawei
import com.hybrid.projectarea.domain.model.ProjectHuaweiTitle
import com.hybrid.projectarea.domain.model.ShowProjectHuaweiCode
import com.hybrid.projectarea.domain.model.checkListMobile
import com.hybrid.projectarea.domain.model.checkListTools
import com.hybrid.projectarea.domain.model.checklistDay
import com.hybrid.projectarea.domain.model.checklistEpps
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthManager(private val apiService: ApiService) {

    suspend fun login(loginRequest: LoginRequest, authListener: AuthListener) {
        val response = apiService.login(loginRequest)
        if (response.isSuccessful) {
            val authToken = response.body()
            authToken?.let {
                authListener.onLoginSuccess(it)
            }
//                        ?: run {
//                        authListener.onLoginFailed()
//                    }
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMessage = try {
                JSONObject(errorBody).getString("error")
            } catch (e: JSONException) {
                "Ocurrió un error desconocido"
            }
            authListener.onLoginFailed(errorMessage)
        }
    }

    suspend fun user(token: String, id: String, authListener: Users) {
        try {
            val response = apiService.users(token, id)
            if (response.isSuccessful) {
                val authToken = response.body()
                authToken?.let {
                    authListener.onUserSuccess(it)
                }
            } else if (response.code() == 401) {
                authListener.onUserNoAuthenticated()
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    JSONObject(errorBody).getString("error")
                } catch (e: JSONException) {
                    "Ocurrió un error desconocido"
                }
                authListener.onUserFailed(errorMessage)
            }
        } catch (e: Exception) {
            authListener.onUserFailed(e.message ?: "Ocurrió un error")
        }
    }

    suspend fun preproject(token: String, userId: String, authListener: PreProjectListener) {
        try {
            val response = apiService.preproject(token, userId)
            if (response.isSuccessful) {
                val authToken = response.body()
                authToken?.let {
                    authListener.onPreProjectSuccess(it)
                }
            } else if(response.code() == 401) {
                authListener.onPreProjectNoAuthenticated()
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    JSONObject(errorBody).getString("error")
                } catch (e: JSONException) {
                    "Ocurrió un error desconocido"
                }
                authListener.onPreProjectFailed(errorMessage)
            }
        } catch (e: Exception) {
            authListener.onPreProjectFailed(e.message ?: "Ocurrió un error")
        }
    }

    suspend fun preProjectPhoto(
        token: String,
        photoRequest: PhotoRequest,
        authListener: PreProjectAddPhoto
    ) {
        try {
            val response = apiService.addphotoreport(token, photoRequest)
            if (response.isSuccessful) {
                authListener.onPreProjectAddPhotoSuccess()
            } else if(response.code() == 401) {
                authListener.onPreProjectAddPhotoNoAuthenticated()
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    JSONObject(errorBody).getString("error")
                } catch (e: JSONException) {
                    "Ocurrió un error desconocido"
                }
                authListener.onPreProjectAddPhotoFailed(errorMessage)
            }
        } catch (e: Exception){
            authListener.onPreProjectAddPhotoFailed("Error de red: $e")
        }
    }

//    suspend fun codephotopreproject(token: String, id: String, authListener: inCodePhotoPreProject) {
//        try {
//            val response = apiService.codephotopreproject(token, id)
//            if (response.isSuccessful) {
//                val authToken = response.body()
//                authToken?.let {
//                    authListener.onCodePhotoPreProjectSuccess(it)
//                }
////                        ?: run {
////                        authListener.onPreProjectFailed()
////                    }
//            } else if(response.code() == 401) {
//                authListener.onCodePhotoPreProjectNoAuthenticated()
//            } else {
//                val errorBody = response.errorBody()?.string()
//                val errorMessage = try {
//                    JSONObject(errorBody).getString("error")
//                } catch (e: JSONException) {
//                    "Ocurrió un error desconocido"
//                }
//                authListener.onCodePhotoPreProjectFailed(errorMessage)
//            }
//        } catch (e: Exception){
//            authListener.onCodePhotoPreProjectFailed(e.message ?: "Ocurrió un error")
//
//        }
//    }

    suspend fun codephotospecific(token: String, id: String, authListener: inCodePhotoDescription) {
        try{
            val response = apiService.codephotoespecific(token, id)
            if (response.isSuccessful) {
                val authToken = response.body()
                authToken?.let {
                    authListener.onCodePhotoDescriptionPreProjectSuccess(it)
                }
//                        ?: run {
//                        authListener.onPreProjectFailed()
//                    }
            } else if(response.code() == 401) {
                authListener.onCodePhotoDescriptionPreProjectNoAuthenticated()
            } else {
                authListener.onCodePhotoDesrriptionPreProjectFailed()
            }
        } catch (e: Exception){
            authListener.onCodePhotoDesrriptionPreProjectFailed()
        }
    }

    suspend fun funRegisterPhoto(token: String, id: String, authListener: inRegisterPhoto) {
        try{
            val response = apiService.requestRegisterPhoto(token, id)
            if (response.isSuccessful) {
                val authToken = response.body()
                authToken?.let { request ->
                    authListener.onRegisterPhotoSuccess(request)
                }
            } else if(response.code() == 401) {
                authListener.onRegisterPhotoNoAuthenticated()
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    JSONObject(errorBody).getString("error")
                } catch (e: JSONException) {
                    "Ocurrió un error desconocido"
                }
                authListener.onRegisterPhotoFailed(errorMessage)
            }
        } catch (e: Exception){
            authListener.onRegisterPhotoFailed("Error de red: $e")
        }
    }

    suspend fun funlogout(token: String, authListener: Logout) {
        val response = apiService.logout(token)
        if (response.isSuccessful) {
            authListener.onLogoutSuccess()
        } else if(response.code() == 401) {
            authListener.onLogoutNoAuthenticated()
        } else {
            authListener.onLogoutFailed()
        }
    }

    suspend fun funGetProjectHuawei(token: String, authListener: inGetProjectHuawei) {
        val response = apiService.huaweiProject(token)
        if (response.isSuccessful) {
            val authToken = response.body()
            authToken?.let {
                authListener.onProjectHuaweiSuccess(it)
            }
        } else if(response.code() == 401) {
            authListener.onProjectHuaweiNoAuthenticated()
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMessage = try {
                JSONObject(errorBody).getString("error")
            } catch (e: JSONException) {
                "Ocurrio un error desconocido"
            }
            authListener.onProjectHuaweiFailed(errorMessage)
        }
    }

    suspend fun funStorePtojectHuawei(
        token: String,
        formStoreProjectHuawei: FormStoreProjectHuawei,
        authListener: inStoreProjectHuawei
    ) {
        val response = apiService.huaweiProjectStore(token, formStoreProjectHuawei)
        if (response.isSuccessful) {
            authListener.onStoreProjectHuaweiSuccess()
        } else if(response.code() == 401) {
            authListener.onStoreProjectHuaweiNoAuthenticated()
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMessage = try {
                JSONObject(errorBody).getString("error")
            } catch (e: JSONException) {
                "Ocurrio un error desconocido"
            }
            authListener.onStoreProjectHuaweiFailed(errorMessage)
        }
    }

    suspend fun funGetProcessManuals(
        token: String,
        formProcessManuals: FormProcessManuals,
        authListener: inGetProcessManuals
    ) {
        val response = apiService.getProcessManuals(token, formProcessManuals)
        if (response.isSuccessful) {
            val authToken = response.body()
            authToken?.let {
                authListener.onProcessManualsSuccess(it)
            }
        } else if(response.code() == 401) {
            authListener.onProcessManualsNoAuthenticated()
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMessage = try {
                JSONObject(errorBody).getString("error")
            } catch (e: JSONException) {
                "Ocurrio un error desconocido$e"
            }
            authListener.onProcessManualsFailed(errorMessage)
        }
    }

    suspend fun funGetDownloadManuals(token: String, path: String, authListener: inGetDownloadManuls) {
        val response = apiService.getDownloadPdf(token, Download(path))
        if (response.isSuccessful) {
            val authToken = response.body()
            authToken?.let {
                authListener.onDownloadManualsSuccess(it)
            }
        } else if(response.code() == 401) {
            authListener.onDownloadManualsNoAuthenticated()
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMessage = try {
                JSONObject(errorBody).getString("error")
            } catch (e: JSONException) {
                "Ocurrio un error desconocido$e"
            }
            authListener.onDownloadManualsFailed(errorMessage)
        }
    }

    suspend fun funPostCheckListTools(
        token: String,
        checkListTools: checkListTools,
        authListener: incheckListTools
    ) {
        val response = apiService.postStoreCheckListTools(token, checkListTools)
        if (response.isSuccessful) {
            authListener.onStoreCheckListToolsSuccess()
        } else if(response.code() == 401) {
            authListener.onStoreCheckListToolsNoAuthenticated()
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMessage = try {
                JSONObject(errorBody).getString("error")
            } catch (e: JSONException) {
                "Ocurrio un error desconocido: $e"
            }
            authListener.onStoreCheckListToolsFailed(errorMessage)
        }
    }

    suspend fun funCheckListMobile(
        token: String,
        checkListMovil: checkListMobile,
        authListener: inCheckListMovil
    ) {
        val response = apiService.postStoreCheckListMobile(token, checkListMovil)
        if (response.isSuccessful) {
            authListener.onStoreCheckListMobileSuccess()
        } else if(response.code() == 401) {
            authListener.onStoreCheckListMobileNoAuthenticated()
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMessage = try {
                JSONObject(errorBody).getString("error")
            } catch (e: JSONException) {
                "Ocurrio un error desconocido: $e"
            }
            authListener.onStoreCheckListMobileFailed(errorMessage)
        }
    }

    suspend fun funCheckListEpps(
        token: String,
        checkListEpps: checklistEpps,
        authListener: inCheckListEpps
    ) {
        val response = apiService.postStoreCheckListEpps(token, checkListEpps)
        if (response.isSuccessful) {
            authListener.onStoreCheckListEppsSuccess()
        } else if(response.code() == 401) {
            authListener.onStoreCheckListEppsNoAuthenticated()
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMessage = try {
                JSONObject(errorBody).getString("error")
            } catch (e: JSONException) {
                "Ocurrio un error desconocido: $e"
            }
            authListener.onStoreCheckListEppsFailed(errorMessage)
        }
    }

    suspend fun funCheckListDay(token: String, checkListDay: checklistDay, authListener: inCheckListDay) {
        val response = apiService.postStoreCheckListDay(token, checkListDay)
        if (response.isSuccessful) {
            authListener.onStoreCheckListDaySuccess()
        } else if(response.code() == 401) {
            authListener.onStoreCheckListDayNoAuthenticated()
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMessage = try {
                JSONObject(errorBody).getString("error")
            } catch (e: JSONException) {
                "Ocurrio un error desconocido: $e"
            }
            authListener.onStoreCheckListDayFailed(errorMessage)
        }
    }

    suspend fun funCheckListHistory(token: String, authListener: inCheckListHistory) {
        val response = apiService.checklistHistory(token)
        if (response.isSuccessful) {
            val authToken = response.body()
            authToken?.let {
                authListener.onStoreCheckListHistorySuccess(it)
            }
        } else if(response.code() == 401) {
            authListener.onStoreCheckListHistoryNoAuthenticated()
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMessage = try {
                JSONObject(errorBody).getString("error")
            } catch (e: JSONException) {
                "Ocurrio un error desconocido$e"
            }
            authListener.onStoreCheckListHistoryFailed(errorMessage)
        }
    }

    suspend fun funExpenseForm(token: String, expenseForm: ExpenseForm, authListener: inExpenseForm) {
        val response = apiService.expenseStore(token, expenseForm)
        if (response.isSuccessful) {
            authListener.onExpenseFormSuccess()
        } else if(response.code() == 401) {
            authListener.onExpenseFormNoAuthenticated()
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMessage = try {
                JSONObject(errorBody).getString("error")
            } catch (e: JSONException) {
                "Ocurrio un error desconocido: $e"
            }
            authListener.onExpenseFormFailed(errorMessage)
        }
    }

    suspend fun funExpenseHistory(token: String, authListener: inExpenseHistory) {
        val response = apiService.expenseHistory(token)
        if (response.isSuccessful) {
            val authToken = response.body()
            authToken?.let {
                authListener.onExpenseHistorySuccess(it)
            }
        } else if(response.code() == 401) {
            authListener.onExpenseHistoryNoAuthenticated()
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMessage = try {
                JSONObject(errorBody).getString("error")
            } catch (e: JSONException) {
                "Ocurrio un error desconocido$e"
            }
            authListener.onExpenseHistoryFailed(errorMessage)
        }
    }

    suspend fun funProjectHuaweiTitleCode(token: String, id: String, authListener: inProjectHuaweiTitleCode) {
        val response = apiService.pointProjectHuaweiTitleCode(token, id)
        if (response.isSuccessful) {
            val authToken = response.body()
            authToken?.let {
                authListener.onProjectHuaweiTitleCodeSuccess(it)
            }
        } else if(response.code() == 401) {
            authListener.onProjectHuaweiTitleCodeNoAuthenticated()
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMessage = try {
                JSONObject(errorBody).getString("error")
            } catch (e: JSONException) {
                "Ocurrió un error desconocido"
            }
            authListener.onProjectHuaweiTitleCodeFailed(errorMessage)
        }
    }

    suspend fun funShowProjectHuaweiCode(token: String, id: String, authListener: inShowProjectHuaweiCode) {
        val response = apiService.pointShowProjectHuaweiCode(token, id)
        if (response.isSuccessful) {
            val authToken = response.body()
            authToken?.let {
                authListener.onShowprojectHuaweiCodeSuccess(it)
            }
        } else if(response.code() == 401) {
            authListener.onShowprojectHuaweiCodeNoAuthenticated()
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMessage = try {
                JSONObject(errorBody).getString("error")
            } catch (e: JSONException) {
                "Ocurrió un error desconocido"
            }
            authListener.onShowprojectHuaweiCodeFailed(errorMessage)
        }
    }

    suspend fun funStoreImageProjectHuawei(token: String,photoRequest: PhotoRequest , authListener: inStoreImageProjectHuawei) {
        val response = apiService.storeImagesProjectHuawei(token, photoRequest)
        if (response.isSuccessful) {
            authListener.onStoreImageProjectHuaweiSuccess()
        } else if(response.code() == 401) {
            authListener.onStoreImageProjectHuaweiNoAuthenticated()
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMessage = try {
                JSONObject(errorBody).getString("error")
            } catch (e: JSONException) {
                "Ocurrio un error desconocido: $e"
            }
            authListener.onStoreImageProjectHuaweiFailed(errorMessage)
        }
    }

    suspend fun funHistoryImageProjectHuawei(token: String,code_id: String , authListener: inRegisterPhoto) {
        val response = apiService.pointHistoryImageProjectHuawei(token, code_id)
        if (response.isSuccessful) {
            val authToken = response.body()
            authToken?.let {
                authListener.onRegisterPhotoSuccess(it)
            }
        } else if(response.code() == 401) {
            authListener.onRegisterPhotoNoAuthenticated()
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMessage = try {
                JSONObject(errorBody).getString("error")
            } catch (e: JSONException) {
                "Ocurrio un error desconocido$e"
            }
            authListener.onRegisterPhotoFailed(errorMessage)
        }
    }

    interface AuthListener {
        fun onLoginSuccess(response: LoginResponse)
        fun onLoginFailed(errorMessage: String)
    }

    interface Users {
        fun onUserSuccess(response: UsersResponse)
        fun onUserNoAuthenticated()
        fun onUserFailed(errorMessage: String)
    }

    interface PreProjectListener {
        fun onPreProjectSuccess(response: List<ElementPreProjectRecyclerView>)
        fun onPreProjectNoAuthenticated()
        fun onPreProjectFailed(error: String)
    }

    interface inCodePhotoPreProject {
        fun onCodePhotoPreProjectSuccess(response: List<PreprojectTitle>)
        fun onCodePhotoPreProjectNoAuthenticated()
        fun onCodePhotoPreProjectFailed(errorMessage: String)
    }

    interface inCodePhotoDescription {
        fun onCodePhotoDescriptionPreProjectSuccess(response: CodePhotoDescription)
        fun onCodePhotoDescriptionPreProjectNoAuthenticated()
        fun onCodePhotoDesrriptionPreProjectFailed()
    }

    interface PreProjectAddPhoto {
        fun onPreProjectAddPhotoSuccess()
        fun onPreProjectAddPhotoNoAuthenticated()
        fun onPreProjectAddPhotoFailed(errorMessage: String)
    }

    interface inRegisterPhoto {
        fun onRegisterPhotoSuccess(response: List<Photo>)
        fun onRegisterPhotoNoAuthenticated()
        fun onRegisterPhotoFailed(errorMessage: String)
    }

    interface Logout {
        fun onLogoutSuccess()
        fun onLogoutNoAuthenticated()
        fun onLogoutFailed()
    }

    interface inGetProjectHuawei {
        fun onProjectHuaweiSuccess(response: List<ProjectHuawei>)
        fun onProjectHuaweiNoAuthenticated()
        fun onProjectHuaweiFailed(errorMessage: String)
    }

    interface inStoreProjectHuawei {
        fun onStoreProjectHuaweiSuccess()
        fun onStoreProjectHuaweiNoAuthenticated()
        fun onStoreProjectHuaweiFailed(errorMessage: String)
    }

    interface inGetProcessManuals {
        fun onProcessManualsSuccess(response: FolderArchiveResponse)
        fun onProcessManualsNoAuthenticated()
        fun onProcessManualsFailed(errorMessage: String)
    }

    interface inGetDownloadManuls {
        fun onDownloadManualsSuccess(response: ResponseBody)
        fun onDownloadManualsNoAuthenticated()
        fun onDownloadManualsFailed(errorMessage: String)
    }

    interface incheckListTools {
        fun onStoreCheckListToolsSuccess()
        fun onStoreCheckListToolsNoAuthenticated()
        fun onStoreCheckListToolsFailed(errorMessage: String)
    }

    interface inCheckListMovil {
        fun onStoreCheckListMobileSuccess()
        fun onStoreCheckListMobileNoAuthenticated()
        fun onStoreCheckListMobileFailed(errorMessage: String)
    }

    interface inCheckListEpps {
        fun onStoreCheckListEppsSuccess()
        fun onStoreCheckListEppsNoAuthenticated()
        fun onStoreCheckListEppsFailed(errorMessage: String)
    }

    interface inCheckListDay {
        fun onStoreCheckListDaySuccess()
        fun onStoreCheckListDayNoAuthenticated()
        fun onStoreCheckListDayFailed(errorMessage: String)
    }

    interface inCheckListHistory {
        fun onStoreCheckListHistorySuccess(response: List<ChecklistHistory>)
        fun onStoreCheckListHistoryNoAuthenticated()
        fun onStoreCheckListHistoryFailed(errorMessage: String)
    }

    interface inExpenseForm {
        fun onExpenseFormSuccess()
        fun onExpenseFormNoAuthenticated()
        fun onExpenseFormFailed(errorMessage: String)
    }

    interface inExpenseHistory {
        fun onExpenseHistorySuccess(response: List<ExpenseHistory>)
        fun onExpenseHistoryNoAuthenticated()
        fun onExpenseHistoryFailed(errorMessage: String)
    }

    interface inProjectHuaweiTitleCode {
        fun onProjectHuaweiTitleCodeSuccess(response: List<ProjectHuaweiTitle>)
        fun onProjectHuaweiTitleCodeNoAuthenticated()
        fun onProjectHuaweiTitleCodeFailed(errorMessage: String)
    }

    interface inShowProjectHuaweiCode {
        fun onShowprojectHuaweiCodeSuccess(response: ShowProjectHuaweiCode)
        fun onShowprojectHuaweiCodeNoAuthenticated()
        fun onShowprojectHuaweiCodeFailed(errorMessage: String)
    }

    interface inStoreImageProjectHuawei {
        fun onStoreImageProjectHuaweiSuccess()
        fun onStoreImageProjectHuaweiNoAuthenticated()
        fun onStoreImageProjectHuaweiFailed(errorMessage: String)
    }
}