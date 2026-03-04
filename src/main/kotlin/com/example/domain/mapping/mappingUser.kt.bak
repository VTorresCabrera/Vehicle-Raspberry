package com.example.domain.mapping

import com.example.data.persistence.models.UserDao
import com.example.domain.models.Role
import com.example.domain.models.User

fun UserDaoToUser(userDao: UserDao): User {
    return User(
        username = userDao.username,
        email = userDao.email,
        password = userDao.password,
        description = userDao.description,
        phone = userDao.phone,
        urlImage = userDao.urlImage,
        active = userDao.active,
        token = userDao.token,
        role = userDao.role,
        id = userDao.id.value
    )
}