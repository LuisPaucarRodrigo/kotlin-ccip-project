package com.hybrid.projectarea.api

import com.hybrid.projectarea.domain.model.ChecklistHistory
import com.hybrid.projectarea.domain.model.CodePhotoDescription
import com.hybrid.projectarea.domain.model.Download
import com.hybrid.projectarea.domain.model.ExpenseForm
import com.hybrid.projectarea.domain.model.ExpenseHistory
import com.hybrid.projectarea.domain.model.FolderArchiveResponse
import com.hybrid.projectarea.domain.model.FormProcessManuals
import com.hybrid.projectarea.domain.model.FormStoreProjectHuawei
import com.hybrid.projectarea.domain.model.ImageReport
import com.hybrid.projectarea.domain.model.LoginRequest
import com.hybrid.projectarea.domain.model.LoginResponse
import com.hybrid.projectarea.domain.model.Photo
import com.hybrid.projectarea.domain.model.PreProject
import com.hybrid.projectarea.domain.model.PreprojectTitle
import com.hybrid.projectarea.domain.model.ProjectHuawei
import com.hybrid.projectarea.domain.model.ProjectHuaweiTitle
import com.hybrid.projectarea.domain.model.ShowProjectHuaweiCode
import com.hybrid.projectarea.domain.model.UsersResponse
import com.hybrid.projectarea.domain.model.checkListTools
import com.hybrid.projectarea.domain.model.checkListVehicle
import com.hybrid.projectarea.domain.model.checklistDiary
import com.hybrid.projectarea.domain.model.checklistEpps
import okhttp3.ResponseBody

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
    suspend fun users(@Path("id") id:String): Response<UsersResponse>

    @GET("preproject/{id}")
    suspend fun preproject( @Path("id") id:String): Response<List<PreProject>>

    @POST("preprojectimage")
    suspend fun postImageReport(@Body imageReport: ImageReport): Response<Void>

    @GET("preproject/code/{id}")
    suspend fun codephotopreproject(@Path("id") id: String): Response<List<PreprojectTitle>>

    @GET("codephotospecific/{id}")
    suspend fun codephotoespecific(@Path("id") id: String): Response<CodePhotoDescription>

    @GET("register/photo/{id}")
    suspend fun requestRegisterPhoto(@Path("id") id: String): Response<List<Photo>>

    @POST("logout")
    suspend fun logout(): Response<Void>

    @GET("huaweiproject/index")
    suspend fun huaweiProject():Response<List<ProjectHuawei>>

    @POST("huaweiproject/store")
    suspend fun huaweiProjectStore( @Body createProject: FormStoreProjectHuawei): Response<Void>

    @GET("huaweiproject/{huawei_project_id}/stages/get")
    suspend fun pointProjectHuaweiTitleCode(@Path("huawei_project_id") id: String):Response<List<ProjectHuaweiTitle>>

    @GET("huaweiproject/{code}/code/get")
    suspend fun pointShowProjectHuaweiCode(@Path("code") id: String):Response<ShowProjectHuaweiCode>

    @POST("huaweiproject/stages/codes/store_image")
    suspend fun storeImagesProjectHuawei( @Body imageReport: ImageReport): Response<Void>

    @GET("huaweiproject/{code}/images/get")
    suspend fun pointHistoryImageProjectHuawei(@Path("code") id: String):Response<List<Photo>>

    @POST("processmanuals/index")
    suspend fun getProcessManuals( @Body formDataACHuawei: FormProcessManuals): Response<FolderArchiveResponse>

    @POST("processmanuals/folder_archive_download")
    @Streaming
    suspend fun getDownloadPdf( @Body formDownload: Download): Response<ResponseBody>

    @POST("checklisttoolkit")
    suspend fun postStoreCheckListTools( @Body checkListTools: checkListTools): Response<Void>

    @POST("checklistcar")
    suspend fun postStoreCheckListVehicle( @Body checkListVehicle: checkListVehicle): Response<Void>

    @POST("checklistepp")
    suspend fun postStoreCheckListEpps( @Body checkListEpps: checklistEpps): Response<Void>

    @POST("checklistdailytoolkit")
    suspend fun postStoreCheckListDay( @Body checkListDay: checklistDiary): Response<Void>

    @GET("checklistHistory")
    suspend fun checklistHistory(): Response<List<ChecklistHistory>>

    @POST("expense/store")
    suspend fun expenseStore( @Body expenseForm: ExpenseForm): Response<Void>

    @GET("expense/index")
    suspend fun expenseHistory(): Response<List<ExpenseHistory>>
}