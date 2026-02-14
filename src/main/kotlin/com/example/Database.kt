package com.example

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import com.example.data.persistence.models.VehicleTable

/*
1.- Cuando llamamos a configureDatabases(), el objeto que devuelve lo gestiona
directamente Exposed y en el momento que realicemos una transacción dentro de un suspendTransaction, Exposed
utiliza el objeto de conexión de manera automaticamente.
2.- El flujo de ejecución sería:
    a).- Obtenemos parámetros de la bbdd y realizamos la conexión, donde Exposed lo manejará por nosotros cuando haga falta.
    b).- Realizamos cualquier llamada al repositorio, con lo que utiliza suspendTransaction que inicializa una transacción en la que
    hará un aconsulta de datos a la bbdd. Cuando necesite de la conexión, Exposed se la proporcionará de manera automática.
    c) ktor, utiliza operaciones asíncronas utilizando corrutinas. Cuando utilizamos suspend, las operaciones a la bbdd que son bloqueantes,
    se convierten en no bloqueantes. suspendTransaction encapsula transacciones a la bbdd, a partir de funciones suspend, por lo que
    deja que el hilo sea liberado a la respuesta de la bbdd. Lo más razonable, es correr la corrutina en el hilo de IO.
 */
fun Application.configureDatabases(){
    val driver = environment.config.property("ktor.database.driver").getString()
    val url = environment.config.property("ktor.database.url").getString()
    val username = environment.config.property("ktor.database.username").getString()
    val password = environment.config.property("ktor.database.password").getString()

    try {
        Database.connect(
            url = url,
            driver = driver,
            user = username,
            password = password
        )
        log.info ("He establecido bien la conexión")
        transaction {
            SchemaUtils.create(VehicleTable)
        }
    }catch (e: Exception){
        log.error("Database connection failed: ${e.message}")
    }


}