package com.example.data.persistence.models

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object VehicleTable : IdTable<String>("Vehicle") {
    override val id: Column<org.jetbrains.exposed.dao.id.EntityID<String>> = varchar("id", 50).entityId()
    val marca = varchar("marca", 100)
    val modelo = varchar("modelo", 100)
    val año = integer("year")
    val precio = double("price")
    val kilometros = integer("kilometers")
    val potencia = integer("power")
    val imagen = varchar("image", 255).nullable()

    override val primaryKey = PrimaryKey(id)
}
