package com.example.domain.mapping

import com.example.data.persistence.models.VehicleDao
import com.example.domain.models.Vehicle

fun VehicleDaoToVehicle(vehicleDao: VehicleDao): Vehicle {
    return Vehicle(
        id = vehicleDao.id.value,
        marca = vehicleDao.marca,
        modelo = vehicleDao.modelo,
        año = vehicleDao.año,
        precio = vehicleDao.precio,
        kilometros = vehicleDao.kilometros,
        potencia = vehicleDao.potencia,
        imagen = vehicleDao.imagen
    )
}