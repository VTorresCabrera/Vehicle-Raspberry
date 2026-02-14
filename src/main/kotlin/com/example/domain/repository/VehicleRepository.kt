package com.example.domain.repository

import com.example.domain.models.Vehicle
import com.example.domain.models.UpdateVehicle

interface VehicleRepository {
    suspend fun getAllVehicles() : List<Vehicle>

    suspend fun getVehiclesByBrand(marca: String) : List<Vehicle>

    suspend fun getVehicleById(id: String) : Vehicle?

    suspend fun postVehicle(vehicle: Vehicle) : Boolean

    suspend fun updateVehicle(vehicle: UpdateVehicle, id: String) : Boolean

    suspend fun deleteVehicle(id: String) : Boolean
}
