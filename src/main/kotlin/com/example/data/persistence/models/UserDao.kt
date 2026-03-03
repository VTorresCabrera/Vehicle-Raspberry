package com.example.data.persistence.models

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserDao(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, UserDao>(UserTable)

    var username by UserTable.username
    var email by UserTable.email
    var password by UserTable.password
    var description by UserTable.description
    var phone by UserTable.phone
    var urlImage by UserTable.urlImage
    var active by UserTable.active
    var token by UserTable.token
    var role by UserTable.role
    val vehicles by VehicleDao referrersOn VehicleTable.user // Inverse relationship
}
