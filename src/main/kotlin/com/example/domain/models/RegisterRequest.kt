package com.example.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val id: String,
    val username: String,
    val email: String,
    val password: String,
    val description: String? = null,
    val phone: String? = null,
    val urlImage: String? = null
)
