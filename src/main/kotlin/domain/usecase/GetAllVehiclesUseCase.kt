package com.domain.usecase

import com.domain.models.Vehicle
import com.domain.repository.VehicleRepository

class GetAllVehiclesUseCase(private val repository: VehicleRepository) {
    suspend operator fun invoke(): List<Vehicle> = repository.getAllVehicles()
}
