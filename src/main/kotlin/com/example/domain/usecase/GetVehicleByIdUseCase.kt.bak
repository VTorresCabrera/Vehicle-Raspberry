package com.example.domain.usecase

import com.example.domain.models.Vehicle
import com.example.domain.repository.VehicleRepository

class GetVehicleByIdUseCase(private val repository: VehicleRepository) {
    var id: String? = null

    suspend operator fun invoke(): Vehicle? {
        return if (id.isNullOrBlank()) {
            null
        } else {
            repository.getVehicleById(id!!)
        }
    }
}
