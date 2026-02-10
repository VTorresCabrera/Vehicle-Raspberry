package com.domain.usecase

import com.domain.models.UpdateVehicle
import com.domain.repository.VehicleRepository

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
