package com.data.inmemory.models

import com.domain.models.Vehicle

object VehicleData {
    val listVehicles = mutableListOf<Vehicle>(
        Vehicle(
            id = "1",
            marca = "Toyota",
            modelo = "Corolla",
            año = 2022,
            precio = 25000.0,
            kilometros = 15000,
            potencia = 140,
            imagen = "https://example.com/toyota-corolla.jpg"
        ),
        Vehicle(
            id = "2",
            marca = "Honda",
            modelo = "Civic",
            año = 2021,
            precio = 23000.0,
            kilometros = 20000,
            potencia = 158,
            imagen = "https://example.com/honda-civic.jpg"
        ),
        Vehicle(
            id = "3",
            marca = "Ford",
            modelo = "Mustang",
            año = 2023,
            precio = 55000.0,
            kilometros = 5000,
            potencia = 450,
            imagen = "https://example.com/ford-mustang.jpg"
        ),
        Vehicle(
            id = "4",
            marca = "Tesla",
            modelo = "Model 3",
            año = 2022,
            precio = 45000.0,
            kilometros = 10000,
            potencia = 283,
            imagen = "https://example.com/tesla-model3.jpg"
        ),
        Vehicle(
            id = "5",
            marca = "BMW",
            modelo = "Series 3",
            año = 2020,
            precio = 35000.0,
            kilometros = 30000,
            potencia = 255,
            imagen = "https://example.com/bmw-series3.jpg"
        )
    )
}