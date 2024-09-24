package com.hybrid.projectarea.domain.model

import com.google.gson.annotations.SerializedName

data class PhotoRequest(
    val id: String,
    val description: String,
    val photo: String,
    val site: String? = null,
    val latitude: String? = null,
    val longitude: String? = null,
)

data class ElementPreProjectRecyclerView(
    @SerializedName("preproject_id") val id: String,
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

data class PreprojectTitle(
    val id: String,
    val type: String,
    val preproject_codes: List<CodePhotoPreProject>
)

data class CodePhotoPreProject(
    val id: String,
    val code: Code,
    val status: String?,
    val replaceable_status: String,
    val rejected_quantity: Int
)

data class Code(
    val code: String
)

data class CodePhotoDescription(
    val id: String,
    val codePreproject: String,
    val code: String,
    val description: String,
    val status: String,
    val images: List<Images>
)

data class Images(
    val image: String
)

data class Photo(
    val image: String,
    val observation: String,
    val state: String,
)