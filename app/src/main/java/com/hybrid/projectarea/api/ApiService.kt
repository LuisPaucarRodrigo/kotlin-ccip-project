package com.hybrid.projectarea.api

import com.hybrid.projectarea.model.CodePhotoDescription
import com.hybrid.projectarea.model.CodePhotoPreProject
import com.hybrid.projectarea.model.ElementPreProjectRecyclerView
import com.hybrid.projectarea.model.FormReportPreProject
import com.hybrid.projectarea.model.FormReportProject
import com.hybrid.projectarea.model.LoginRequest
import com.hybrid.projectarea.model.LoginResponse
import com.hybrid.projectarea.model.Photo
import com.hybrid.projectarea.model.ProjectFind
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
    fun addphotoreport(@Header("Authorization") token: String,@Body photoRequest: FormReportPreProject): Call<Void>

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
    fun storephoto(@Header("Authorization") token: String, @Body photoRequest: FormReportProject): Call<Void>

    @POST("logout")
    fun logout(@Header("Authorization") token: String): Call<Void>

}