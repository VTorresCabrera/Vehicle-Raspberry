package com.example.data.persistence.repository

import com.example.data.persistence.models.UserDao
import com.example.data.persistence.models.UserTable
import com.example.data.persistence.models.suspendTransaction
import com.example.domain.mapping.UserDaoToUser
import com.example.domain.models.UpdateUser
import com.example.domain.models.User
import com.example.domain.repository.UserRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update

class PersistenceUserRepository : UserRepository {

    override suspend fun getAllUsers(): List<User> = suspendTransaction {
        UserDao.all().map(::UserDaoToUser)
    }

    override suspend fun getUserById(id: String): User? = suspendTransaction {
        UserDao.findById(id)?.let(::UserDaoToUser)
    }

    override suspend fun getUserByEmail(email: String): User? = suspendTransaction {
        UserDao.find { UserTable.email eq email }.singleOrNull()?.let(::UserDaoToUser)
    }

    override suspend fun insertUser(user: User): Boolean {
        if (getUserByEmail(user.email) != null) return false
        try {
            suspendTransaction {
                UserDao.new(user.id) {
                    username = user.username
                    email = user.email
                    password = user.password
                    description = user.description
                    phone = user.phone
                    urlImage = user.urlImage
                    active = user.active
                    token = user.token
                    role = user.role
                }
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun updateUser(updateUser: UpdateUser, id: String): Boolean {
        return try {
            suspendTransaction {
                UserTable.update({ UserTable.id eq id }) { stm ->
                    updateUser.username?.let { stm[username] = it }
                    updateUser.email?.let { stm[email] = it }
                    updateUser.password?.let { stm[password] = it }
                    updateUser.description?.let { stm[description] = it }
                    updateUser.phone?.let { stm[phone] = it }
                    updateUser.urlImage?.let { stm[urlImage] = it }
                    updateUser.active?.let { stm[active] = it }
                    updateUser.role?.let { stm[role] = it }
                    updateUser.token?.let { stm[token] = it }
                } > 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun deleteUser(id: String): Boolean = suspendTransaction {
        UserTable.deleteWhere { UserTable.id eq id } > 0
    }
}
