package com.example.domain.usecase

import com.example.domain.models.Vehicle
import com.example.domain.repository.VehicleRepository

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
