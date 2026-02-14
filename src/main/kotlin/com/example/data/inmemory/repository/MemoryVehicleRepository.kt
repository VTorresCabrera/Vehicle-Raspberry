package com.example.data.inmemory.repository

import com.example.data.inmemory.models.VehicleData
import com.example.domain.models.UpdateVehicle
import com.example.domain.models.Vehicle
import com.example.domain.repository.VehicleRepository

class MemoryVehicleRepository : VehicleRepository {

    override suspend fun getAllVehicles(): List<Vehicle> {
        return VehicleData.listVehicles
    }

    override suspend fun getVehiclesByBrand(marca: String): List<Vehicle> {
        return VehicleData.listVehicles.filter { it.marca.equals(marca, ignoreCase = true) }
    }

    override suspend fun getVehicleById(id: String): Vehicle? {
        return VehicleData.listVehicles.find { it.id == id }
    }

    override suspend fun postVehicle(vehicle: Vehicle): Boolean {
        if (getVehicleById(vehicle.id) != null) {
            return false
        }
        VehicleData.listVehicles.add(vehicle)
        return true
    }

    override suspend fun updateVehicle(vehicle: UpdateVehicle, id: String): Boolean {
        val index = VehicleData.listVehicles.indexOfFirst { it.id == id }
        return if (index != -1) {
            val original = VehicleData.listVehicles[index]
            VehicleData.listVehicles[index] = original.copy(
                marca = vehicle.marca ?: original.marca,
                modelo = vehicle.modelo ?: original.modelo,
                año = vehicle.año ?: original.año,
                precio = vehicle.precio ?: original.precio,
                kilometros = vehicle.kilometros ?: original.kilometros,
                potencia = vehicle.potencia ?: original.potencia,
                imagen = vehicle.imagen ?: original.imagen
            )
            true
        } else {
            false
        }
    }

    override suspend fun deleteVehicle(id: String): Boolean {
        val index = VehicleData.listVehicles.indexOfFirst { it.id == id }
        return if (index != -1) {
            VehicleData.listVehicles.removeAt(index)
            true
        } else {
            false
        }
    }
}
