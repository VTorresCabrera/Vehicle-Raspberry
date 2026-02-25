package com.example.data.persistence.repository

import com.example.data.persistence.models.VehicleDao
import com.example.data.persistence.models.VehicleTable
import com.example.data.persistence.models.UserTable
import com.example.data.persistence.models.suspendTransaction
import com.example.domain.mapping.VehicleDaoToVehicle
import com.example.domain.models.UpdateVehicle
import com.example.domain.models.Vehicle
import com.example.domain.repository.VehicleRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update
import com.example.data.persistence.models.UserDao

class PersistenceVehicleRepository : VehicleRepository {

    override suspend fun getAllVehicles(): List<Vehicle> {
        return suspendTransaction {
            VehicleDao.all().map(::VehicleDaoToVehicle)
        }
    }

    override suspend fun getVehiclesByBrand(marca: String): List<Vehicle> {
        return suspendTransaction {
            VehicleDao.find { VehicleTable.marca eq marca }.map(::VehicleDaoToVehicle)
        }
    }

    override suspend fun getVehicleById(id: String): Vehicle? {
        return suspendTransaction {
            VehicleDao.findById(id)?.let { VehicleDaoToVehicle(it) }
        }
    }

    override suspend fun postVehicle(vehicle: Vehicle): Boolean {
        if (getVehicleById(vehicle.id) != null) return false
        try {
            suspendTransaction {
                val userRef = UserDao.findById(vehicle.userId) ?: throw Exception("User not found")
                VehicleDao.new(vehicle.id) {
                    this.marca = vehicle.marca
                    this.modelo = vehicle.modelo
                    this.año = vehicle.año
                    this.precio = vehicle.precio
                    this.kilometros = vehicle.kilometros
                    this.potencia = vehicle.potencia
                    this.imagen = vehicle.imagen
                    this.status = vehicle.status
                    this.user = userRef
                }
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun updateVehicle(vehicle: UpdateVehicle, id: String): Boolean {
        var num = 0
        try {
            suspendTransaction {
                num = VehicleTable.update({ VehicleTable.id eq id }) { stm ->
                    vehicle.marca?.let { stm[marca] = it }
                    vehicle.modelo?.let { stm[modelo] = it }
                    vehicle.año?.let { stm[año] = it }
                    vehicle.precio?.let { stm[precio] = it }
                    vehicle.kilometros?.let { stm[kilometros] = it }
                    vehicle.potencia?.let { stm[potencia] = it }
                    vehicle.imagen?.let { stm[imagen] = it }
                    vehicle.status?.let { stm[status] = it }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return num == 1
    }

    override suspend fun deleteVehicle(id: String): Boolean = suspendTransaction {
        val num = VehicleTable.deleteWhere { VehicleTable.id eq id }
        num == 1
    }

    override suspend fun getVehiclesByUserId(userId: String): List<Vehicle> = suspendTransaction {
        VehicleDao.find { VehicleTable.user eq userId }.map(::VehicleDaoToVehicle)
    }

    override suspend fun deleteVehiclesByUserId(userId: String): Boolean = suspendTransaction {
        VehicleTable.deleteWhere { VehicleTable.user eq userId } > 0
    }
}
