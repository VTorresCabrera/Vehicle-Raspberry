package com.domain.models
import kotlinx.serialization.Serializable

@Serializable
data class UpdateVehicle(
    val marca: String? = null,
    val modelo: String? = null,
    val año: Int? = null,
    val precio: Double? = null,
    val kilometros: Int? = null,
    val potencia: Int? = null,
    val imagen: String? = null
)
