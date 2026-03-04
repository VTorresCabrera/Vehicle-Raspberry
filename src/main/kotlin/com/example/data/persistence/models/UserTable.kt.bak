package com.example.data.persistence.models

import com.example.domain.models.Role
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object UserTable : IdTable<String>("User") {
    override val id: Column<EntityID<String>> = varchar("id", 50).entityId()
    val username = varchar("username", 100)
    val email = varchar("email", 100).uniqueIndex() // Unique constraint
    val password = varchar("password", 255)
    val description = varchar("description", 255)
    val phone = varchar("phone", 20)
    val urlImage = varchar("urlImage", 255).nullable()
    val active = bool("active").default(true)
    val token = varchar("token", 255).nullable()
    val role = enumerationByName("role", 20, Role::class).default(Role.USER) // Enum storage

    override val primaryKey = PrimaryKey(id)
}
