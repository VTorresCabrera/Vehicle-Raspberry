package com.domain.security

interface PasswordHashInterface {
    fun hash(pass: String): String  //
    fun verify(pass: String, passHash: String): Boolean
}