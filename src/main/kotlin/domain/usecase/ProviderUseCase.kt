package com.domain.usecase

import com.data.inmemory.repository.MemoryVehicleRepository
import com.data.persistence.repository.PersistenceVehicleRepository
import com.domain.models.*
import com.domain.repository.VehicleRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object ProviderUseCase {

    // private val repository: VehicleRepository = MemoryVehicleRepository()
    private val repository: VehicleRepository = PersistenceVehicleRepository()
    val logger: Logger = LoggerFactory.getLogger("VehicleUseCaseLogger")

    private val getAllVehiclesUseCase = GetAllVehiclesUseCase(repository)
    private val getVehicleByIdUseCase = GetVehicleByIdUseCase(repository)
    private val updateVehicleUseCase = UpdateVehicleUseCase(repository)
    private val insertVehicleUseCase = InsertVehicleUseCase(repository)
    private val getVehiclesByBrandUseCase = GetVehiclesByBrandUseCase(repository)
    private val deleteVehicleUseCase = DeleteVehicleUseCase(repository)

    suspend fun getAllVehicles() = getAllVehiclesUseCase()

    suspend fun getVehicleById(id: String): Vehicle? {
        if (id.isBlank()) {
            logger.warn("El ID está vacío. No podemos buscar un vehículo.")
            return null
        }
        getVehicleByIdUseCase.id = id
        val vehicle = getVehicleByIdUseCase()
        return if (vehicle == null) {
            logger.warn("No se ha encontrado un vehículo con el ID: $id.")
            null
        } else {
            vehicle
        }
    }

    suspend fun insertVehicle(vehicle: Vehicle?): Boolean {
        if (vehicle == null) {
            logger.warn("No existen datos del vehículo a insertar.")
            return false
        }
        insertVehicleUseCase.vehicle = vehicle
        val res = insertVehicleUseCase()
        return if (!res) {
            logger.warn("No se ha insertado el vehículo. Posiblemente ya exista.")
            false
        } else {
            true
        }
    }

    suspend fun updateVehicle(updateVehicle: UpdateVehicle?, id: String): Boolean {
        if (updateVehicle == null) {
            logger.warn("No existen datos del vehículo a actualizar.")
            return false
        }
        updateVehicleUseCase.updateVehicle = updateVehicle
        updateVehicleUseCase.id = id
        return updateVehicleUseCase()
    }

    suspend fun getVehiclesByBrand(brand: String): List<Vehicle> {
        getVehiclesByBrandUseCase.brand = brand
        return getVehiclesByBrandUseCase()
    }

    suspend fun deleteVehicle(id: String): Boolean {
        deleteVehicleUseCase.id = id
        return deleteVehicleUseCase()
    }
}