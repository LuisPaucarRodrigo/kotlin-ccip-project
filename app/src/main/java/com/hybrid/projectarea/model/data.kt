package com.hybrid.projectarea.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val dni: String,
    val password: String
)

data class LoginResponse(
    val id: String,
    val token: String
)

data class UsersResponse(
    val id: String,
    val name: String,
    val dni: String,
    val email: String
)

data class PhotoRequest(
    val id: String,
    val description: String,
    val photo: String,
    val latitude: String? = null,
    val longitude: String? = null,
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
    val state: String,
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
