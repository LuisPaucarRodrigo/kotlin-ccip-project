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

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Streaming

interface ApiService {

    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("users/{id}")
    fun users(@Header("Authorization") token: String,@Path("id") id:String): Call<UsersResponse>

    @GET("preproject/{id}")
    fun preproject(@Header("Authorization") token: String, @Path("id") id:String): Call<List<ElementPreProjectRecyclerView>>

    @POST("preprojectimage")
    fun addphotoreport(@Header("Authorization") token: String,@Body photoRequest: PhotoRequest): Call<Void>

    @GET("preproject/code/{id}")
    fun codephotopreproject(@Header("Authorization") token: String,@Path("id") id: String): Call<List<PreprojectTitle>>

    @GET("codephotospecific/{id}")
    fun codephotoespecific(@Header("Authorization") token: String,@Path("id") id: String): Call<CodePhotoDescription>

    @GET("register/photo/{id}")
    fun requestRegisterPhoto(@Header("Authorization") token: String,@Path("id") id: String): Call<List<Photo>>

    @GET("project")
    fun project(@Header("Authorization") token: String): Call<List<ProjectRecycler>>

    @GET("project/show/{id}")
    fun projectshow(@Header("Authorization") token: String,@Path("id") id: String): Call<ProjectFind>

    @POST("project/store/image")
    fun storephoto(@Header("Authorization") token: String, @Body photoRequest: PhotoRequest): Call<Void>

    @POST("logout")
    fun logout(@Header("Authorization") token: String): Call<Void>

    @GET("huaweiproject/index")
    fun huaweiProject(@Header("Authorization") token: String):Call<List<ProjectHuawei>>

    @POST("huaweiproject/store")
    fun huaweiProjectStore(@Header("Authorization") token: String, @Body createProject: FormStoreProjectHuawei): Call<Void>

    @GET("huaweiproject/{huawei_project_id}/stages/get")
    fun pointProjectHuaweiTitleCode(@Header("Authorization") token: String,@Path("huawei_project_id") id: String):Call<List<ProjectHuaweiTitle>>

    @GET("huaweiproject/{code}/code/get")
    fun pointShowProjectHuaweiCode(@Header("Authorization") token: String,@Path("code") id: String):Call<ShowProjectHuaweiCode>

    @POST("huaweiproject/stages/codes/store_image")
    fun storeImagesProjectHuawei(@Header("Authorization") token: String, @Body photoRequest: PhotoRequest): Call<Void>

    @GET("huaweiproject/{code}/images/get")
    fun pointHistoryImageProjectHuawei(@Header("Authorization") token: String,@Path("code") id: String):Call<List<Photo>>

    @POST("storeDatosACHuawei")
    fun storeDatosACHuawei(@Header("Authorization") token:String, @Body formDataACHuawei: FormDataACHuawei): Call<Void>

    @GET("RectifiersHuawei/{id}")
    fun rectifiersProjectHuawei(@Header("Authorization") token:String,@Path("id") id:String): Call<List<NameRectifiers>>

    @POST("processmanuals/index")
    fun getProcessManuals(@Header("Authorization") token: String, @Body formDataACHuawei: FormProcessManuals): Call<FolderArchiveResponse>

    @POST("processmanuals/folder_archive_download")
    @Streaming
    fun getDownloadPdf(@Header("Authorization") token: String, @Body formDownload: Download): Call<ResponseBody>

    @POST("checklisttoolkit")
    fun postStoreCheckListTools(@Header("Authorization") token: String, @Body checkListTools: checkListTools): Call<Void>

    @POST("checklistcar")
    fun postStoreCheckListMobile(@Header("Authorization") token: String, @Body checkListMobile: checkListMobile): Call<Void>

    @POST("checklistepp")
    fun postStoreCheckListEpps(@Header("Authorization") token: String, @Body checkListEpps: checklistEpps): Call<Void>

    @POST("checklistdailytoolkit")
    fun postStoreCheckListDay(@Header("Authorization") token: String, @Body checkListDay: checklistDay): Call<Void>

    @GET("checklistHistory")
    fun checklistHistory(@Header("Authorization") token:String): Call<List<ChecklistHistory>>

    @POST("expense/store")
    fun expenseStore(@Header("Authorization") token: String, @Body expenseForm:ExpenseForm): Call<Void>

    @GET("expense/index")
    fun expenseHistory(@Header("Authorization") token:String): Call<List<ExpenseHistory>>
}