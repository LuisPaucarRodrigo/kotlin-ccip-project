package com.hybrid.projectarea.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("dni") val dni: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("token") val token: String
)

data class UsersResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("dni") val dni: String,
    @SerializedName("email") val email: String
)

data class PhotoRequest(
    @SerializedName("id") val id: String,
    @SerializedName("description") val description: String,
    @SerializedName("photo") val photo: String
)

data class ElementPreProjectRecyclerView(
    val id: String,
    val code: String,
    val description: String,
    val date: String,
    val observation: String,
)

data class ProjectRecycler(
    val id: String,
    val code: String,
    val description: String,
)

data class ProjectFind(
    val id: String,
    val code: String,
    val description: String,
)

data class CodePhotoPreProject(
    val id: String,
    val code: String,
    val status: String
)

data class CodePhotoDescription(
    val id: String,
    val codePreproject: String,
    val code: String,
    val description: String,
    val status: String,
)

data class Photo(
    val image: String,
    val observation: String,
    val state: String
)

data class ProjectHuawei(
    val id: String,
    val site: String,
    val elaborated: String,
    val code: String,
    val name: String,
    val address: String,
    val reference: String,
    val access: String
)

data class FormStoreProjectHuawei(
    val site: String,
    val elaborated: String,
    val code: String,
    val name: String,
    val address: String,
    val reference: String,
    val access: String
)


data class FormDataACHuawei(
    val power: String,
    val concessionaire: String,
    val supply: String,
    val type: String,
    val caliber: String,
    val fuses: String,
    val calibertg: String,
    val itm: String,
    val powere: String,
    val brand: String,
    val tankCapacity: String,
    val typee: String,
    val tableTransfer: String,
    val capacity: String,
    val fijacion: String,
    val typet: String,
    val section: String,
    val itmMajor: String,
    val rs: String,
    val rt: String,
    val st: String,
    val r: String,
    val s: String,
    val t: String
)

data class NameRectifiers(
    val id: String,
    val brand:String,
)
