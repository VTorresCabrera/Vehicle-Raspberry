package com.example.domain.usecase

import com.example.data.persistence.repository.PersistenceUserRepository
import com.example.data.persistence.repository.PersistenceVehicleRepository
import com.example.domain.models.*
import com.example.domain.repository.UserRepository
import com.example.domain.repository.VehicleRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.mindrot.jbcrypt.BCrypt

object ProviderUseCase {

    private val vehicleRepository: VehicleRepository = PersistenceVehicleRepository()
    private val userRepository: UserRepository = PersistenceUserRepository()
    val logger: Logger = LoggerFactory.getLogger("VehicleUseCaseLogger")

    // User Operations
    suspend fun insertUser(user: User): Boolean {
        return userRepository.insertUser(user)
    }

    suspend fun getUserById(id: String): User? {
        return userRepository.getUserById(id)
    }

    suspend fun getUserByEmail(email: String): User? {
        return userRepository.getUserByEmail(email)
    }

    suspend fun getAllUsers(): List<User> {
        return userRepository.getAllUsers()
    }

    suspend fun updateUser(updateUser: UpdateUser, id: String): Boolean {
        return userRepository.updateUser(updateUser, id)
    }

    suspend fun deleteUser(id: String): Boolean {
        // Also delete vehicles?
        // On Delete Cascade is usually handled by DB, but here we can do it manually or rely on DB
        // UserTable doesn't have cascade delete explicitly set in my definition, so manual delete vehicles first
        vehicleRepository.deleteVehiclesByUserId(id)
        return userRepository.deleteUser(id)
    }

    // Vehicle Operations
    suspend fun getVehiclesByUserId(userId: String): List<Vehicle> {
        return vehicleRepository.getVehiclesByUserId(userId)
    }

    suspend fun insertVehicle(vehicle: Vehicle): Boolean {
        // Validation: Check if user exists? Repository insert relies on FK constraint
        return vehicleRepository.postVehicle(vehicle)
    }

    suspend fun getVehicleById(id: String): Vehicle? {
        return vehicleRepository.getVehicleById(id)
    }

    suspend fun updateVehicle(updateVehicle: UpdateVehicle, id: String): Boolean {
        return vehicleRepository.updateVehicle(updateVehicle, id)
    }

    suspend fun deleteVehicle(id: String): Boolean {
        return vehicleRepository.deleteVehicle(id)
    }
    
    suspend fun deleteVehiclesByUserId(userId: String): Boolean {
        return vehicleRepository.deleteVehiclesByUserId(userId)
    }
    
    suspend fun getAllVehicles(): List<Vehicle> {
        return vehicleRepository.getAllVehicles()
    }
}