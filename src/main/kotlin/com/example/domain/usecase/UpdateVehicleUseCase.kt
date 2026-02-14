package com.example.domain.usecase

import com.example.domain.models.UpdateVehicle
import com.example.domain.repository.VehicleRepository

class UpdateVehicleUseCase(private val repository: VehicleRepository) {
    var updateVehicle: UpdateVehicle? = null
    var id: String? = null

    suspend operator fun invoke(): Boolean {
        return if (updateVehicle == null || id == null) {
            false
        } else {
            repository.updateVehicle(updateVehicle!!, id!!)
        }
    }
}
