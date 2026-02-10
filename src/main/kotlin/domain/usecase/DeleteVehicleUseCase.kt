package com.domain.usecase

import com.domain.repository.VehicleRepository

class DeleteVehicleUseCase(private val repository: VehicleRepository) {
    var id: String? = null

    suspend operator fun invoke(): Boolean {
        return if (id == null) {
            false
        } else {
            repository.deleteVehicle(id!!)
        }
    }
}
