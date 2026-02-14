package com.example.domain.usecase

import com.example.domain.models.Vehicle
import com.example.domain.repository.VehicleRepository

class GetVehiclesByBrandUseCase(private val repository: VehicleRepository) {
    var brand: String? = null

    suspend operator fun invoke(): List<Vehicle> {
        return brand?.let {
            repository.getVehiclesByBrand(it)
        } ?: emptyList()
    }
}
