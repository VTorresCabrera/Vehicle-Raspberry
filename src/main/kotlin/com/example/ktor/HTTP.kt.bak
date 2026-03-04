package com.example.ktor

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureHTTP() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        
        // Allow requests from all hosts for development convenience
        anyHost()
        
        // Alternatively, update this to specifically allow your frontend's origin
        // allowHost("localhost:5173", schemes = listOf("http", "https"))
    }
}
