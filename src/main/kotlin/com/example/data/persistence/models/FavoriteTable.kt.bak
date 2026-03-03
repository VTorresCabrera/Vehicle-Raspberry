package com.example.data.persistence.models

import org.jetbrains.exposed.sql.Table

/**
 * Favorites per user.
 * Composite primary key (user_id, vehicle_id).
 */
object FavoriteTable : Table("Favorite") {
    val user = reference("user_id", UserTable)
    val vehicle = reference("vehicle_id", VehicleTable)
    val createdAt = long("created_at").clientDefault { System.currentTimeMillis() }

    override val primaryKey = PrimaryKey(user, vehicle)
}
