package com.example.data.persistence.models

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

/*
Contendrá una función suspendida que crará una transacción individual para cada consulta
a la BBDD,
Dicha función, debe ser asíncrona y que permita suspenderse sin afectar en ningún momento
la ejecución del hilo principal. Por ello debemos de ejecutarla en el scope de la IO.

Dicha función de transacción, deberá recibir una lambda o bloque de código, que será parate
de una función de extensión de la clase Transaction. El porqué, es porque dese ese bloque de código,
el receptor que en este caso es Transaction, en dicha función de extensión debe acceder a contenido de
la clase Transaction. Debe devolver un Genérico T.

newSuspendendTransaction, acepta dos argumentos. Uno el scope donde queremos ejecutarla y otro el código
que representa la consulta.

Recordamos, que para que la función trabaje con genéricos, hay que anteponerle al nombre <T>
Kotlin, maneja los tipos y los genericos de manera implícita, es decir, T será lo que devuelva la lambda en
donde sea invocada, que será List<Vehicle>
 */



suspend fun <T> suspendTransaction ( code : Transaction.() -> T) : T =
    newSuspendedTransaction(Dispatchers.IO, statement= code)