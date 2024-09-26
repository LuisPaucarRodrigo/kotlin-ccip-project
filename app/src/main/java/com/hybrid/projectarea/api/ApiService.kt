package com.hybrid.projectarea.api

import com.hybrid.projectarea.domain.model.ChecklistHistory
import com.hybrid.projectarea.domain.model.CodePhotoDescription
import com.hybrid.projectarea.domain.model.Download
import com.hybrid.projectarea.domain.model.ElementPreProjectRecyclerView
import com.hybrid.projectarea.domain.model.ExpenseForm
import com.hybrid.projectarea.domain.model.ExpenseHistory
import com.hybrid.projectarea.domain.model.FolderArchiveResponse
import com.hybrid.projectarea.domain.model.FormProcessManuals
import com.hybrid.projectarea.domain.model.FormStoreProjectHuawei
import com.hybrid.projectarea.domain.model.LoginRequest
import com.hybrid.projectarea.domain.model.LoginResponse
import com.hybrid.projectarea.domain.model.Photo
import com.hybrid.projectarea.domain.model.PhotoRequest
import com.hybrid.projectarea.domain.model.PreprojectTitle
import com.hybrid.projectarea.domain.model.ProjectHuawei
import com.hybrid.projectarea.domain.model.ProjectHuaweiTitle
import com.hybrid.projectarea.domain.model.ShowProjectHuaweiCode
import com.hybrid.projectarea.domain.model.UsersResponse
import com.hybrid.projectarea.domain.model.checkListMobile
import com.hybrid.projectarea.domain.model.checkListTools
import com.hybrid.projectarea.domain.model.checklistDay
import com.hybrid.projectarea.domain.model.checklistEpps
import okhttp3.ResponseBody

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Streaming

interface ApiService {

    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET("users/{id}")
    suspend fun users(@Header("Authorization") token: String,@Path("id") id:String): Response<UsersResponse>

    @GET("preproject/{id}")
    suspend fun preproject(@Header("Authorization") token: String, @Path("id") id:String): Response<List<ElementPreProjectRecyclerView>>

    @POST("preprojectimage")
    suspend fun addphotoreport(@Header("Authorization") token: String,@Body photoRequest: PhotoRequest): Response<Void>

    @GET("preproject/code/{id}")
    suspend fun codephotopreproject(@Header("Authorization") token: String,@Path("id") id: String): Response<List<PreprojectTitle>>

    @GET("codephotospecific/{id}")
    suspend fun codephotoespecific(@Header("Authorization") token: String,@Path("id") id: String): Response<CodePhotoDescription>

    @GET("register/photo/{id}")
    suspend fun requestRegisterPhoto(@Header("Authorization") token: String,@Path("id") id: String): Response<List<Photo>>

    @POST("logout")
    suspend fun logout(@Header("Authorization") token: String): Response<Void>

    @GET("huaweiproject/index")
    suspend fun huaweiProject(@Header("Authorization") token: String):Response<List<ProjectHuawei>>

    @POST("huaweiproject/store")
    suspend fun huaweiProjectStore(@Header("Authorization") token: String, @Body createProject: FormStoreProjectHuawei): Response<Void>

    @GET("huaweiproject/{huawei_project_id}/stages/get")
    suspend fun pointProjectHuaweiTitleCode(@Header("Authorization") token: String,@Path("huawei_project_id") id: String):Response<List<ProjectHuaweiTitle>>

    @GET("huaweiproject/{code}/code/get")
    suspend fun pointShowProjectHuaweiCode(@Header("Authorization") token: String,@Path("code") id: String):Response<ShowProjectHuaweiCode>

    @POST("huaweiproject/stages/codes/store_image")
    suspend fun storeImagesProjectHuawei(@Header("Authorization") token: String, @Body photoRequest: PhotoRequest): Response<Void>

    @GET("huaweiproject/{code}/images/get")
    suspend fun pointHistoryImageProjectHuawei(@Header("Authorization") token: String,@Path("code") id: String):Response<List<Photo>>

    @POST("processmanuals/index")
    suspend fun getProcessManuals(@Header("Authorization") token: String, @Body formDataACHuawei: FormProcessManuals): Response<FolderArchiveResponse>

    @POST("processmanuals/folder_archive_download")
    @Streaming
    suspend fun getDownloadPdf(@Header("Authorization") token: String, @Body formDownload: Download): Response<ResponseBody>

    @POST("checklisttoolkit")
    suspend fun postStoreCheckListTools(@Header("Authorization") token: String, @Body checkListTools: checkListTools): Response<Void>

    @POST("checklistcar")
    suspend fun postStoreCheckListMobile(@Header("Authorization") token: String, @Body checkListMobile: checkListMobile): Response<Void>

    @POST("checklistepp")
    suspend fun postStoreCheckListEpps(@Header("Authorization") token: String, @Body checkListEpps: checklistEpps): Response<Void>

    @POST("checklistdailytoolkit")
    suspend fun postStoreCheckListDay(@Header("Authorization") token: String, @Body checkListDay: checklistDay): Response<Void>

    @GET("checklistHistory")
    suspend fun checklistHistory(@Header("Authorization") token:String): Response<List<ChecklistHistory>>

    @POST("expense/store")
    suspend fun expenseStore(@Header("Authorization") token: String, @Body expenseForm: ExpenseForm): Response<Void>

    @GET("expense/index")
    suspend fun expenseHistory(@Header("Authorization") token:String): Response<List<ExpenseHistory>>
}