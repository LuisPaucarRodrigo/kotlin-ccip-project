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