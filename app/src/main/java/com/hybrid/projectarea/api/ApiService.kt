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

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("users")
    fun users(@Header("Authorization") token: String): Call<UsersResponse>

    @GET("preproject")
    fun preproject(@Header("Authorization") token: String): Call<List<ElementPreProjectRecyclerView>>

    @POST("preprojectimage")
    fun addphotoreport(@Header("Authorization") token: String,@Body photoRequest: PhotoRequest): Call<Void>

    @GET("preproject/code/{id}")
    fun codephotopreproject(@Header("Authorization") token: String,@Path("id") id: String): Call<List<CodePhotoPreProject>>

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

    @POST("storeDatosACHuawei")
    fun storeDatosACHuawei(@Header("Authorization") token:String, @Body formDataACHuawei: FormDataACHuawei): Call<Void>

    @GET("RectifiersHuawei/{id}")
    fun rectifiersProjectHuawei(@Header("Authorization") token:String,@Path("id") id:String): Call<List<NameRectifiers>>
}