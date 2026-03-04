package com.example.domain.repository

import com.example.domain.models.UpdateUser
import com.example.domain.models.User

interface UserRepository {
    suspend fun getAllUsers(): List<User>
    suspend fun getUserById(id: String): User?
    suspend fun getUserByEmail(email: String): User?
    suspend fun insertUser(user: User): Boolean
    suspend fun updateUser(updateUser: UpdateUser, id: String): Boolean
    suspend fun deleteUser(id: String): Boolean
}
