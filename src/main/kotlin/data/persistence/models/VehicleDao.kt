package com.data.persistence.models

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID

class VehicleDao(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, VehicleDao>(VehicleTable)

    var marca by VehicleTable.marca
    var modelo by VehicleTable.modelo
    var año by VehicleTable.año
    var precio by VehicleTable.precio
    var kilometros by VehicleTable.kilometros
    var potencia by VehicleTable.potencia
    var imagen by VehicleTable.imagen
}
