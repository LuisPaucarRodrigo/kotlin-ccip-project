package com.hybrid.projectarea.api

import com.hybrid.projectarea.domain.model.Photo
import com.hybrid.projectarea.domain.model.Download
import com.hybrid.projectarea.domain.model.FolderArchiveResponse
import com.hybrid.projectarea.domain.model.FormProcessManuals
import com.hybrid.projectarea.domain.model.FormStoreProjectHuawei
import com.hybrid.projectarea.domain.model.ImageReport
import com.hybrid.projectarea.domain.model.ProjectHuawei
import com.hybrid.projectarea.domain.model.ProjectHuaweiTitle
import com.hybrid.projectarea.domain.model.ShowProjectHuaweiCode
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject

class AuthManager(private val apiService: ApiService) {

    suspend fun funlogout( authListener: Logout) {
        val response = apiService.logout()
        if (response.isSuccessful) {
            authListener.onLogoutSuccess()
        } else if(response.code() == 401) {
            authListener.onLogoutNoAuthenticated()
        } else {
            authListener.onLogoutFailed()
        }
    }

    suspend fun funGetProjectHuawei( authListener: inGetProjectHuawei) {
        val response = apiService.huaweiProject()
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

        formStoreProjectHuawei: FormStoreProjectHuawei,
        authListener: inStoreProjectHuawei
    ) {
        val response = apiService.huaweiProjectStore(formStoreProjectHuawei)
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

        formProcessManuals: FormProcessManuals,
        authListener: inGetProcessManuals
    ) {
        val response = apiService.getProcessManuals(formProcessManuals)
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

    suspend fun funGetDownloadManuals( path: String, authListener: inGetDownloadManuls) {
        val response = apiService.getDownloadPdf(Download(path))
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

    suspend fun funProjectHuaweiTitleCode( id: String, authListener: inProjectHuaweiTitleCode) {
        val response = apiService.pointProjectHuaweiTitleCode(id)
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

    suspend fun funShowProjectHuaweiCode( id: String, authListener: inShowProjectHuaweiCode) {
        val response = apiService.pointShowProjectHuaweiCode(id)
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

    suspend fun funStoreImageProjectHuawei(photoRequest: ImageReport , authListener: inStoreImageProjectHuawei) {
        val response = apiService.storeImagesProjectHuawei(photoRequest)
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

    suspend fun funHistoryImageProjectHuawei(code_id: String , authListener: inRegisterPhoto) {
        val response = apiService.pointHistoryImageProjectHuawei(code_id)
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