package com.example.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUser(
    val username: String? = null,
    val email: String? = null,
    val password: String? = null,
    val description: String? = null,
    val phone: String? = null,
    val urlImage: String? = null,
    val active: Boolean? = null,
    val role: Role? = null
)
