package com.domain.usecase

import com.domain.models.Vehicle
import com.domain.repository.VehicleRepository

class GetVehiclesByBrandUseCase(private val repository: VehicleRepository) {
    var brand: String? = null

    suspend operator fun invoke(): List<Vehicle> {
        return brand?.let {
            repository.getVehiclesByBrand(it)
        } ?: emptyList()
    }
}
