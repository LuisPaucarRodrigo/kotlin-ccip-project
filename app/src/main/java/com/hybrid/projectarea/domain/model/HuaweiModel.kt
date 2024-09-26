package com.hybrid.projectarea.domain.model

data class ProjectHuawei(
    val id: String,
    val assigned_diu: String,
    val huawei_site: HuaweiSite,
    val code: String,
)

data class HuaweiSite(
    val id: String,
    val name: String
)

data class FormStoreProjectHuawei(
    val site: String,
    val diu: String,
)

data class ProjectHuaweiTitle(
    val id: String,
    val description: String,
    val huawei_project_codes: List<ProjectHuaweiCode>
)

data class ProjectHuaweiCode(
    val id: String,
    val huawei_code: HuaweiCode,
    val status: Int,
    val state: Int,
    val rejected_quantity: Int
)

data class HuaweiCode(
    val id: String,
    val code: String
)

data class ShowProjectHuaweiCode(
    val id: String,
    val project_code: String,
    val code: String,
    val description: String,
    val code_status: Int,
)