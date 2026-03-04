package com.example.domain.models

import kotlinx.serialization.Serializable

/**
 * Response DTO that matches the Android app expectations.
 * We do NOT return password/role/active.
 */
@Serializable
data class ApiUserResponse(
    val id: String,
    val username: String,
    val email: String,
    val token: String,
    val description: String? = null,
    val phone: String? = null,
    val urlImage: String? = null
)
