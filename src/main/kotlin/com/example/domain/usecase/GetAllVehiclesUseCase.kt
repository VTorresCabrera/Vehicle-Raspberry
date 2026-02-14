package com.example.domain.usecase

import com.example.domain.models.Vehicle
import com.example.domain.repository.VehicleRepository

class GetAllVehiclesUseCase(private val repository: VehicleRepository) {
    suspend operator fun invoke(): List<Vehicle> = repository.getAllVehicles()
}
