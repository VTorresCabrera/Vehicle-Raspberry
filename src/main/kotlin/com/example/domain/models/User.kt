package com.example.domain.models

import kotlinx.serialization.Serializable

@Serializable
enum class Role {
    USER, ADMIN
}

@Serializable
data class User(
    val username: String,
    val email: String,
    val password: String, // Note: Should be hashed, but prompt says "logueo en la BBDD"
    val description: String,
    val phone: String,
    val urlImage: String? = null,
    val active: Boolean = true,
    val token: String? = null,
    val role: Role = Role.USER,
    val id: String
)
