package com.domain.usecase

import com.domain.models.Vehicle
import com.domain.repository.VehicleRepository

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
