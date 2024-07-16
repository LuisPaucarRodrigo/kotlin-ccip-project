package com.hybrid.projectarea.api


import com.hybrid.projectarea.model.CodePhotoDescription
import com.hybrid.projectarea.model.CodePhotoPreProject
import com.hybrid.projectarea.model.ElementPreProjectRecyclerView
import com.hybrid.projectarea.model.FormDataACHuawei
import com.hybrid.projectarea.model.FormStoreProjectHuawei
import com.hybrid.projectarea.model.LoginRequest
import com.hybrid.projectarea.model.LoginResponse
import com.hybrid.projectarea.model.NameRectifiers
import com.hybrid.projectarea.model.Photo
import com.hybrid.projectarea.model.PhotoRequest
import com.hybrid.projectarea.model.ProjectFind
import com.hybrid.projectarea.model.ProjectHuawei
import com.hybrid.projectarea.model.ProjectRecycler
import com.hybrid.projectarea.model.UsersResponse
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthManager(private val apiService: ApiService) {

    fun login(loginRequest: LoginRequest, authListener: AuthListener) {
        val call = apiService.login(loginRequest)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
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
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                authListener.onLoginFailed("Error de red: ${t.message}")
            }
        })
    }

    fun user(token: String, id:String, authListener: Users) {
        val call = apiService.users(token,id)
        call.enqueue(object : Callback<UsersResponse> {
            override fun onResponse(call: Call<UsersResponse>, response: Response<UsersResponse>) {
                if (response.isSuccessful) {
                    val authToken = response.body()
                    authToken?.let {
                        authListener.onUserSuccess(it)
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorBody).getString("error")
                    } catch (e: JSONException) {
                        "Ocurrió un error desconocido"
                    }
                    authListener.onUserFailed(errorMessage)
                }
            }
            override fun onFailure(call: Call<UsersResponse>, t: Throwable) {
                authListener.onUserFailed("${t.message}")
            }
        })
    }

    fun preproject(token: String, userId:String, authListener: PreProjectListener) {
        val call = apiService.preproject(token,userId)
        call.enqueue(object : Callback<List<ElementPreProjectRecyclerView>> {
            override fun onResponse(call: Call<List<ElementPreProjectRecyclerView>>, response: Response<List<ElementPreProjectRecyclerView>>) {
                if (response.isSuccessful) {
                    val authToken = response.body()
                    authToken?.let {
                        authListener.onPreProjectSuccess(it)
                    }
//                        ?: run {
//                        authListener.onPreProjectFailed()
//                    }
                } else {
                    authListener.onPreProjectFailed("")
                }
            }
            override fun onFailure(call: Call<List<ElementPreProjectRecyclerView>>, t: Throwable) {
                authListener.onPreProjectFailed("${t.message}")
            }
        })
    }

    fun preProjectPhoto(token: String,photoRequest: PhotoRequest, authListener: PreProjectAddPhoto) {
        val call = apiService.addphotoreport(token,photoRequest)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful ) {
                    authListener.onPreProjectAddPhotoSuccess()
                }else{
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorBody).getString("error")
                    } catch (e: JSONException) {
                        "Ocurrió un error desconocido"
                    }
                    authListener.onPreProjectAddPhotoFailed(errorMessage)
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                authListener.onPreProjectAddPhotoFailed("Error de red: ${t.message}")
            }
        })
    }

    fun codephotopreproject(token: String,id: String, authListener: inCodePhotoPreProject) {
        val call = apiService.codephotopreproject(token,id)
        call.enqueue(object : Callback<List<CodePhotoPreProject>> {
            override fun onResponse(call: Call<List<CodePhotoPreProject>>, response: Response<List<CodePhotoPreProject>>) {
                if (response.isSuccessful) {
                    val authToken = response.body()
                    authToken?.let {
                        authListener.onCodePhotoPreProjectSuccess(it)
                    }
//                        ?: run {
//                        authListener.onPreProjectFailed()
//                    }
                } else {
                    authListener.onCodePhotoPreProjectFailed()
                }
            }
            override fun onFailure(call: Call<List<CodePhotoPreProject>>, t: Throwable) {
                authListener.onCodePhotoPreProjectFailed()
            }
        })
    }

    fun codephotospecific(token: String,id: String, authListener: inCodePhotoDescription) {
        val call = apiService.codephotoespecific(token,id)
        call.enqueue(object : Callback<CodePhotoDescription> {
            override fun onResponse(call: Call<CodePhotoDescription>, response: Response<CodePhotoDescription>) {
                if (response.isSuccessful) {
                    val authToken = response.body()
                    authToken?.let {
                        authListener.onCodePhotoDescriptionPreProjectSuccess(it)
                    }
//                        ?: run {
//                        authListener.onPreProjectFailed()
//                    }
                } else {
                    authListener.onCodePhotoDesrriptionPreProjectFailed()
                }
            }
            override fun onFailure(call: Call<CodePhotoDescription>, t: Throwable) {
                authListener.onCodePhotoDesrriptionPreProjectFailed()
            }
        })
    }

    fun funRegisterPhoto(token: String, id: String,authListener: inRegisterPhoto) {
        val call = apiService.requestRegisterPhoto(token,id)
        call.enqueue(object : Callback<List<Photo>> {
            override fun onResponse(call: Call<List<Photo>>, response: Response<List<Photo>>) {
                if (response.isSuccessful){
                    val authToken = response.body()
                    authToken?.let { request ->
                        authListener.onRegisterPhotoSuccess(request)
                    }
                } else {
                    authListener.onRegisterPhotoFailed()
                }
            }

            override fun onFailure(call: Call<List<Photo>>, t: Throwable) {
                authListener.onRegisterPhotoFailed()
            }
        })
    }

    fun project(token: String, authListener: ProjectListener) {
        val call = apiService.project(token)
        call.enqueue(object : Callback<List<ProjectRecycler>> {
            override fun onResponse(call: Call<List<ProjectRecycler>>, response: Response<List<ProjectRecycler>>) {
                if (response.isSuccessful) {
                    val authToken = response.body()
                    authToken?.let { request ->
                        authListener.onProjectSuccess(request)
                    }
//                        ?: run {
//                        authListener.onPreProjectFailed()
//                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorBody).getString("error")
                    } catch (e: JSONException) {
                        "Ocurrió un error desconocido"
                    }
                    authListener.onProjectFailed(errorMessage)
                }
            }
            override fun onFailure(call: Call<List<ProjectRecycler>>, t: Throwable) {
                authListener.onProjectFailed("Error de red: ${t.message}")
            }
        })
    }

    fun projectshow(token: String,id: String, authListener: ProjectShow) {
        val call = apiService.projectshow(token,id)
        call.enqueue(object : Callback<ProjectFind> {
            override fun onResponse(call: Call<ProjectFind>, response: Response<ProjectFind>) {
                if (response.isSuccessful) {
                    val authToken = response.body()
                    authToken?.let {
                        authListener.onProjectSpecificSuccess(it)
                    }
//                        ?: run {
//                        authListener.onPreProjectFailed()
//                    }
                } else {
                    authListener.onProjectSpecificFailed()
                }
            }
            override fun onFailure(call: Call<ProjectFind>, t: Throwable) {
                authListener.onProjectSpecificFailed()
            }
        })
    }

    fun projectPhoto(token: String,id: String,description: String, image: String, authListener: ProjectStorePhoto) {
        val photoRequest = PhotoRequest(id,description, image)
        val call = apiService.storephoto(token,photoRequest)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful ) {
                    authListener.onProjectAddPhotoSuccess()
                }else{
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorBody).getString("error")
                    } catch (e: JSONException) {
                        "Ocurrio un error desconocido"
                    }
                    authListener.onProjectAddPhotoFailed(errorMessage)
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                authListener.onProjectAddPhotoFailed("Error de red: ${t.message}")
            }
        })
    }

    fun logout(token: String, authListener: Logout) {
        val call = apiService.logout(token)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    authListener.onLogoutSuccess()
                } else {
                    authListener.onLogoutFailed()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                authListener.onLogoutFailed()
            }
        })
    }

    fun funGetProjectHuawei(token:String, authListener: inGetProjectHuawei){
        val call = apiService.huaweiProject(token)
        call.enqueue(object :Callback<List<ProjectHuawei>>{
            override fun onResponse(call: Call<List<ProjectHuawei>>, response: Response<List<ProjectHuawei>>) {
                if (response.isSuccessful){
                    val authToken = response.body()
                    authToken?.let {
                        authListener.onProjectHuaweiSuccess(it)
                    }
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

            override fun onFailure(call: Call<List<ProjectHuawei>>, t: Throwable) {
                authListener.onProjectHuaweiFailed("${t.message}")
            }

        })
    }

    fun funStorePtojectHuawei(token: String,formStoreProjectHuawei: FormStoreProjectHuawei,authListener: inStoreProjectHuawei){
        val call = apiService.huaweiProjectStore(token,formStoreProjectHuawei)
        call.enqueue(object : Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful){
                    authListener.onStoreProjectHuaweiSuccess()
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

            override fun onFailure(call: Call<Void>, t: Throwable) {
                authListener.onStoreProjectHuaweiFailed("${t.message}")
            }

        })
    }

    fun funFormDataACHuawei(token:String,formDataACHuawei:FormDataACHuawei,authListener: inFormDataACHuawei){
        val call = apiService.storeDatosACHuawei(token,formDataACHuawei)
        call.enqueue(object : Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful){

                    authListener.onStoreFormDataACHuaweiSuccess()
                } else {
                    authListener.onStoreFormDataACHuaweiFailed()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                authListener.onStoreFormDataACHuaweiFailed()
            }

        })
    }

    fun funGetRectifiersProjectHuawei(token:String, id:String, authListener: inGetRectifiersProjectHuawei){
        val call = apiService.rectifiersProjectHuawei(token,id)
        call.enqueue(object :Callback<List<NameRectifiers>>{
            override fun onResponse(call: Call<List<NameRectifiers>>, response: Response<List<NameRectifiers>>) {
                if (response.isSuccessful){
                    val authToken = response.body()
                    authToken?.let {
                        authListener.onRectifiersProjectHuaweiSuccess(it)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorBody).getString("error")
                    } catch (e: JSONException) {
                        "Ocurrio un error desconocido"
                    }
                    authListener.onRectifiersProjectHuaweiFailed(errorMessage)
                }
            }

            override fun onFailure(call: Call<List<NameRectifiers>>, t: Throwable) {
                authListener.onRectifiersProjectHuaweiFailed("${t.message}")
            }

        })
    }



    interface AuthListener {
        fun onLoginSuccess(response: LoginResponse)
        fun onLoginFailed(errorMessage: String)
    }

    interface Users {
        fun onUserSuccess(response: UsersResponse)
        fun onUserFailed(errorMessage: String)
    }

    interface PreProjectListener {
        fun onPreProjectSuccess(response: List<ElementPreProjectRecyclerView>)
        fun onPreProjectFailed(error:String)
    }

    interface inCodePhotoPreProject {
        fun onCodePhotoPreProjectSuccess(response: List<CodePhotoPreProject>)
        fun onCodePhotoPreProjectFailed()
    }

    interface inCodePhotoDescription {
        fun onCodePhotoDescriptionPreProjectSuccess(response: CodePhotoDescription)
        fun onCodePhotoDesrriptionPreProjectFailed()
    }

    interface PreProjectAddPhoto {
        fun onPreProjectAddPhotoSuccess()
        fun onPreProjectAddPhotoFailed(errorMessage: String)
    }

    interface inRegisterPhoto {
        fun onRegisterPhotoSuccess(response: List<Photo>)
        fun onRegisterPhotoFailed()
    }

    interface ProjectListener {
        fun onProjectSuccess(response: List<ProjectRecycler>)
        fun onProjectFailed(error: String)
    }

    interface ProjectShow {
        fun onProjectSpecificSuccess(response: ProjectFind)
        fun onProjectSpecificFailed()
    }

    interface ProjectStorePhoto {
        fun onProjectAddPhotoSuccess()
        fun onProjectAddPhotoFailed(errorMessage: String)
    }

    interface Logout {
        fun onLogoutSuccess()
        fun onLogoutFailed()
    }

    interface inGetProjectHuawei {
        fun onProjectHuaweiSuccess(response:List<ProjectHuawei>)
        fun onProjectHuaweiFailed(errorMessage: String)
    }

    interface inStoreProjectHuawei {
        fun onStoreProjectHuaweiSuccess()
        fun onStoreProjectHuaweiFailed(errorMessage: String)
    }

    interface inFormDataACHuawei {
        fun onStoreFormDataACHuaweiSuccess()
        fun onStoreFormDataACHuaweiFailed()
    }
    interface inGetRectifiersProjectHuawei {
        fun onRectifiersProjectHuaweiSuccess(response:List<NameRectifiers>)
        fun onRectifiersProjectHuaweiFailed(errorMessage: String)
    }
}