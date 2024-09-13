package com.hybrid.projectarea.api


import com.hybrid.projectarea.model.ChecklistHistory
import com.hybrid.projectarea.model.CodePhotoDescription
import com.hybrid.projectarea.model.CodePhotoPreProject
import com.hybrid.projectarea.model.Download
import com.hybrid.projectarea.model.ElementPreProjectRecyclerView
import com.hybrid.projectarea.model.ExpenseForm
import com.hybrid.projectarea.model.ExpenseHistory
import com.hybrid.projectarea.model.FolderArchiveResponse
import com.hybrid.projectarea.model.FormDataACHuawei
import com.hybrid.projectarea.model.FormProcessManuals
import com.hybrid.projectarea.model.FormStoreProjectHuawei
import com.hybrid.projectarea.model.LoginRequest
import com.hybrid.projectarea.model.LoginResponse
import com.hybrid.projectarea.model.NameRectifiers
import com.hybrid.projectarea.model.Photo
import com.hybrid.projectarea.model.PhotoRequest
import com.hybrid.projectarea.model.PreprojectTitle
import com.hybrid.projectarea.model.ProjectFind
import com.hybrid.projectarea.model.ProjectHuawei
import com.hybrid.projectarea.model.ProjectHuaweiTitle
import com.hybrid.projectarea.model.ProjectRecycler
import com.hybrid.projectarea.model.ShowProjectHuaweiCode
import com.hybrid.projectarea.model.UsersResponse
import com.hybrid.projectarea.model.checkListMobile
import com.hybrid.projectarea.model.checkListTools
import com.hybrid.projectarea.model.checklistDay
import com.hybrid.projectarea.model.checklistEpps
import okhttp3.ResponseBody
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

    fun user(token: String, id: String, authListener: Users) {
        val call = apiService.users(token, id)
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

    fun preproject(token: String, userId: String, authListener: PreProjectListener) {
        val call = apiService.preproject(token, userId)
        call.enqueue(object : Callback<List<ElementPreProjectRecyclerView>> {
            override fun onResponse(
                call: Call<List<ElementPreProjectRecyclerView>>,
                response: Response<List<ElementPreProjectRecyclerView>>
            ) {
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

    fun preProjectPhoto(
        token: String,
        photoRequest: PhotoRequest,
        authListener: PreProjectAddPhoto
    ) {
        val call = apiService.addphotoreport(token, photoRequest)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    authListener.onPreProjectAddPhotoSuccess()
                } else {
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

    fun codephotopreproject(token: String, id: String, authListener: inCodePhotoPreProject) {
        val call = apiService.codephotopreproject(token, id)
        call.enqueue(object : Callback<List<PreprojectTitle>> {
            override fun onResponse(
                call: Call<List<PreprojectTitle>>,
                response: Response<List<PreprojectTitle>>
            ) {
                if (response.isSuccessful) {
                    val authToken = response.body()
                    authToken?.let {
                        authListener.onCodePhotoPreProjectSuccess(it)
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
                    authListener.onCodePhotoPreProjectFailed(errorMessage)
                }
            }

            override fun onFailure(call: Call<List<PreprojectTitle>>, t: Throwable) {
                authListener.onCodePhotoPreProjectFailed("${t.message}")
            }
        })
    }

    fun codephotospecific(token: String, id: String, authListener: inCodePhotoDescription) {
        val call = apiService.codephotoespecific(token, id)
        call.enqueue(object : Callback<CodePhotoDescription> {
            override fun onResponse(
                call: Call<CodePhotoDescription>,
                response: Response<CodePhotoDescription>
            ) {
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

    fun funRegisterPhoto(token: String, id: String, authListener: inRegisterPhoto) {
        val call = apiService.requestRegisterPhoto(token, id)
        call.enqueue(object : Callback<List<Photo>> {
            override fun onResponse(call: Call<List<Photo>>, response: Response<List<Photo>>) {
                if (response.isSuccessful) {
                    val authToken = response.body()
                    authToken?.let { request ->
                        authListener.onRegisterPhotoSuccess(request)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorBody).getString("error")
                    } catch (e: JSONException) {
                        "Ocurrió un error desconocido"
                    }
                    authListener.onRegisterPhotoFailed(errorMessage)
                }
            }

            override fun onFailure(call: Call<List<Photo>>, t: Throwable) {
                authListener.onRegisterPhotoFailed("${t.message}")
            }
        })
    }

    fun project(token: String, authListener: ProjectListener) {
        val call = apiService.project(token)
        call.enqueue(object : Callback<List<ProjectRecycler>> {
            override fun onResponse(
                call: Call<List<ProjectRecycler>>,
                response: Response<List<ProjectRecycler>>
            ) {
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

    fun projectshow(token: String, id: String, authListener: ProjectShow) {
        val call = apiService.projectshow(token, id)
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

    fun projectPhoto(
        token: String,
        id: String,
        description: String,
        image: String,
        authListener: ProjectStorePhoto
    ) {
        val photoRequest = PhotoRequest(id, description, image)
        val call = apiService.storephoto(token, photoRequest)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    authListener.onProjectAddPhotoSuccess()
                } else {
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

    fun funGetProjectHuawei(token: String, authListener: inGetProjectHuawei) {
        val call = apiService.huaweiProject(token)
        call.enqueue(object : Callback<List<ProjectHuawei>> {
            override fun onResponse(
                call: Call<List<ProjectHuawei>>,
                response: Response<List<ProjectHuawei>>
            ) {
                if (response.isSuccessful) {
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

    fun funStorePtojectHuawei(
        token: String,
        formStoreProjectHuawei: FormStoreProjectHuawei,
        authListener: inStoreProjectHuawei
    ) {
        val call = apiService.huaweiProjectStore(token, formStoreProjectHuawei)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
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

    fun funFormDataACHuawei(
        token: String,
        formDataACHuawei: FormDataACHuawei,
        authListener: inFormDataACHuawei
    ) {
        val call = apiService.storeDatosACHuawei(token, formDataACHuawei)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
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

    fun funGetRectifiersProjectHuawei(
        token: String,
        id: String,
        authListener: inGetRectifiersProjectHuawei
    ) {
        val call = apiService.rectifiersProjectHuawei(token, id)
        call.enqueue(object : Callback<List<NameRectifiers>> {
            override fun onResponse(
                call: Call<List<NameRectifiers>>,
                response: Response<List<NameRectifiers>>
            ) {
                if (response.isSuccessful) {
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

    fun funGetProcessManuals(
        token: String,
        formProcessManuals: FormProcessManuals,
        authListener: inGetProcessManuals
    ) {
        val call = apiService.getProcessManuals(token, formProcessManuals)
        call.enqueue(object : Callback<FolderArchiveResponse> {
            override fun onResponse(
                call: Call<FolderArchiveResponse>,
                response: Response<FolderArchiveResponse>
            ) {
                if (response.isSuccessful) {
                    val authToken = response.body()
                    authToken?.let {
                        authListener.onProcessManualsSuccess(it)
                    }
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

            override fun onFailure(call: Call<FolderArchiveResponse>, t: Throwable) {
                authListener.onProcessManualsFailed("${t.message}")
            }
        })
    }

    fun funGetDownloadManuals(token: String, path: String, authListener: inGetDownloadManuls) {
        val call = apiService.getDownloadPdf(token, Download(path))
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val authToken = response.body()
                    authToken?.let {
                        authListener.onDownloadManualsSuccess(it)
                    }
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

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                authListener.onDownloadManualsFailed("${t.message}")
            }

        })
    }

    fun funPostCheckListTools(
        token: String,
        checkListTools: checkListTools,
        authListener: incheckListTools
    ) {
        val call = apiService.postStoreCheckListTools(token, checkListTools)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {

                    authListener.onStoreCheckListToolsSuccess()
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

            override fun onFailure(call: Call<Void>, t: Throwable) {
                authListener.onStoreCheckListToolsFailed("${t.message}")
            }

        })
    }

    fun funCheckListMobile(
        token: String,
        checkListMovil: checkListMobile,
        authListener: inCheckListMovil
    ) {
        val call = apiService.postStoreCheckListMobile(token, checkListMovil)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    authListener.onStoreCheckListMobileSuccess()
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

            override fun onFailure(call: Call<Void>, t: Throwable) {
                authListener.onStoreCheckListMobileFailed("${t.message}")

            }
        })
    }

    fun funCheckListEpps(
        token: String,
        checkListEpps: checklistEpps,
        authListener: inCheckListEpps
    ) {
        val call = apiService.postStoreCheckListEpps(token, checkListEpps)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    authListener.onStoreCheckListEppsSuccess()
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

            override fun onFailure(call: Call<Void>, t: Throwable) {
                authListener.onStoreCheckListEppsFailed("${t.message}")
            }

        })
    }

    fun funCheckListDay(token: String, checkListDay: checklistDay, authListener: inCheckListDay) {
        val call = apiService.postStoreCheckListDay(token, checkListDay)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    authListener.onStoreCheckListDaySuccess()
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

            override fun onFailure(call: Call<Void>, t: Throwable) {
                authListener.onStoreCheckListDayFailed("${t.message}")
            }

        })
    }

    fun funCheckListHistory(token: String, authListener: inCheckListHistory) {
        val call = apiService.checklistHistory(token)
        call.enqueue(object : Callback<List<ChecklistHistory>> {
            override fun onResponse(
                call: Call<List<ChecklistHistory>>,
                response: Response<List<ChecklistHistory>>
            ) {
                if (response.isSuccessful) {
                    val authToken = response.body()
                    authToken?.let {
                        authListener.onStoreCheckListHistorySuccess(it)
                    }
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

            override fun onFailure(call: Call<List<ChecklistHistory>>, t: Throwable) {
                authListener.onStoreCheckListHistoryFailed("${t.message}")
            }

        })
    }

    fun funExpenseForm(token: String, expenseForm: ExpenseForm, authListener: inExpenseForm) {
        val call = apiService.expenseStore(token, expenseForm)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    authListener.onExpenseFormSuccess()
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

            override fun onFailure(call: Call<Void>, t: Throwable) {
                authListener.onExpenseFormFailed("${t.message}")
            }

        })
    }

    fun funExpenseHistory(token: String, authListener: inExpenseHistory) {
        val call = apiService.expenseHistory(token)
        call.enqueue(object : Callback<List<ExpenseHistory>> {
            override fun onResponse(
                call: Call<List<ExpenseHistory>>,
                response: Response<List<ExpenseHistory>>
            ) {
                if (response.isSuccessful) {
                    val authToken = response.body()
                    authToken?.let {
                        authListener.onExpenseHistorySuccess(it)
                    }
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

            override fun onFailure(call: Call<List<ExpenseHistory>>, t: Throwable) {
                authListener.onExpenseHistoryFailed("${t.message}")
            }

        })
    }

    fun funProjectHuaweiTitleCode(token: String, id: String, authListener: inProjectHuaweiTitleCode) {
        val call = apiService.pointProjectHuaweiTitleCode(token, id)
        call.enqueue(object :Callback<List<ProjectHuaweiTitle>> {
            override fun onResponse(
                call: Call<List<ProjectHuaweiTitle>>,
                response: Response<List<ProjectHuaweiTitle>>
            ) {
                if (response.isSuccessful) {
                    val authToken = response.body()
                    authToken?.let {
                        authListener.onProjectHuaweiTitleCodeSuccess(it)
                    }
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

            override fun onFailure(call: Call<List<ProjectHuaweiTitle>>, t: Throwable) {
                authListener.onProjectHuaweiTitleCodeFailed("${t.message}")
            }

        })
    }

    fun funShowProjectHuaweiCode(token: String, id: String, authListener: inShowProjectHuaweiCode) {
        val call = apiService.pointShowProjectHuaweiCode(token, id)
        call.enqueue(object :Callback<ShowProjectHuaweiCode>{
            override fun onResponse(
                call: Call<ShowProjectHuaweiCode>,
                response: Response<ShowProjectHuaweiCode>
            ) {
                if (response.isSuccessful) {
                    val authToken = response.body()
                    authToken?.let {
                        authListener.onShowprojectHuaweiCodeSuccess(it)
                    }
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

            override fun onFailure(call: Call<ShowProjectHuaweiCode>, t: Throwable) {
                authListener.onShowprojectHuaweiCodeFailed("${t.message}")
            }

        })
    }

    fun funStoreImageProjectHuawei(token: String,photoRequest: PhotoRequest , authListener: inStoreImageProjectHuawei) {
        val call = apiService.storeImagesProjectHuawei(token, photoRequest)
        call.enqueue(object :Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    authListener.onStoreImageProjectHuaweiSuccess()
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

            override fun onFailure(call: Call<Void>, t: Throwable) {
                authListener.onStoreImageProjectHuaweiFailed("${t.message}")
            }
        })
    }

    fun funHistoryImageProjectHuawei(token: String,code_id: String , authListener: inRegisterPhoto) {
        val call = apiService.pointHistoryImageProjectHuawei(token, code_id)
        call.enqueue(object :Callback<List<Photo>>{
            override fun onResponse(call: Call<List<Photo>>, response: Response<List<Photo>>) {
                if (response.isSuccessful) {
                    val authToken = response.body()
                    authToken?.let {
                        authListener.onRegisterPhotoSuccess(it)
                    }
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
            override fun onFailure(call: Call<List<Photo>>, t: Throwable) {
                authListener.onRegisterPhotoFailed("${t.message}")

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
        fun onPreProjectFailed(error: String)
    }

    interface inCodePhotoPreProject {
        fun onCodePhotoPreProjectSuccess(response: List<PreprojectTitle>)
        fun onCodePhotoPreProjectFailed(errorMessage: String)
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
        fun onRegisterPhotoFailed(errorMessage: String)
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
        fun onProjectHuaweiSuccess(response: List<ProjectHuawei>)
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
        fun onRectifiersProjectHuaweiSuccess(response: List<NameRectifiers>)
        fun onRectifiersProjectHuaweiFailed(errorMessage: String)
    }

    interface inGetProcessManuals {
        fun onProcessManualsSuccess(response: FolderArchiveResponse)
        fun onProcessManualsFailed(errorMessage: String)
    }

    interface inGetDownloadManuls {
        fun onDownloadManualsSuccess(response: ResponseBody)
        fun onDownloadManualsFailed(errorMessage: String)
    }

    interface incheckListTools {
        fun onStoreCheckListToolsSuccess()
        fun onStoreCheckListToolsFailed(errorMessage: String)
    }

    interface inCheckListMovil {
        fun onStoreCheckListMobileSuccess()
        fun onStoreCheckListMobileFailed(errorMessage: String)
    }

    interface inCheckListEpps {
        fun onStoreCheckListEppsSuccess()
        fun onStoreCheckListEppsFailed(errorMessage: String)
    }

    interface inCheckListDay {
        fun onStoreCheckListDaySuccess()
        fun onStoreCheckListDayFailed(errorMessage: String)
    }

    interface inCheckListHistory {
        fun onStoreCheckListHistorySuccess(response: List<ChecklistHistory>)
        fun onStoreCheckListHistoryFailed(errorMessage: String)
    }

    interface inExpenseForm {
        fun onExpenseFormSuccess()
        fun onExpenseFormFailed(errorMessage: String)
    }

    interface inExpenseHistory {
        fun onExpenseHistorySuccess(response: List<ExpenseHistory>)
        fun onExpenseHistoryFailed(errorMessage: String)
    }

    interface inProjectHuaweiTitleCode {
        fun onProjectHuaweiTitleCodeSuccess(response: List<ProjectHuaweiTitle>)
        fun onProjectHuaweiTitleCodeFailed(errorMessage: String)
    }

    interface inShowProjectHuaweiCode {
        fun onShowprojectHuaweiCodeSuccess(response: ShowProjectHuaweiCode)
        fun onShowprojectHuaweiCodeFailed(errorMessage: String)
    }

    interface inStoreImageProjectHuawei {
        fun onStoreImageProjectHuaweiSuccess()
        fun onStoreImageProjectHuaweiFailed(errorMessage: String)
    }
}