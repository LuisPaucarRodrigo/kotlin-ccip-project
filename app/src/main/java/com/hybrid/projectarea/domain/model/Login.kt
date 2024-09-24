package com.hybrid.projectarea.domain.model

data class LoginRequest(
    val dni: String,
    val password: String
)

data class LoginResponse(
    val id: String,
    val token: String
)