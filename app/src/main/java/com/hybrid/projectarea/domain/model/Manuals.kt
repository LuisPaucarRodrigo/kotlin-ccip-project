package com.hybrid.projectarea.domain.model

data class FormProcessManuals(
    val root: String,
    val path: String
)

data class FolderArchiveResponse(
    val folders_archives: List<GetProcessManuals>,
    val currentPath: String,
    val previousPath: String
)

data class GetProcessManuals(
    val name: String,
    val type: String,
    val path: String,
    val size: String?
)

data class Download(
    val path: String
)