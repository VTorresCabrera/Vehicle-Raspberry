package com.domain.usecase

import com.domain.models.Vehicle
import com.domain.repository.VehicleRepository

class InsertVehicleUseCase(private val repository: VehicleRepository) {
    var vehicle: Vehicle? = null

    suspend operator fun invoke(): Boolean {
        return if (vehicle == null) {
            false
        } else {
            repository.postVehicle(vehicle!!)
        }
    }
}
