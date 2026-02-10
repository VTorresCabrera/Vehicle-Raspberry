package com.domain.models
import kotlinx.serialization.Serializable

@Serializable
data class Vehicle(
    val id: String,
    val marca: String,
    val modelo: String,
    val año: Int,
    val precio: Double,
    val kilometros: Int,
    val potencia: Int,
    val imagen: String? = null
)
