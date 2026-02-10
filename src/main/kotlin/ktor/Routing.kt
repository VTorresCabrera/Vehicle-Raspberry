package com.example

import com.domain.models.UpdateVehicle
import com.domain.models.Vehicle
import com.domain.usecase.ProviderUseCase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Vehicle API is running!")
        }

        route("/vehicles") {
            get {
                val vehicles = ProviderUseCase.getAllVehicles()
                call.respond(vehicles)
            }

            get("/{id}") {
                val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing id")
                val vehicle = ProviderUseCase.getVehicleById(id)
                if (vehicle != null) {
                    call.respond(vehicle)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Vehicle not found")
                }
            }

            get("/brand/{brand}") {
                val brand = call.parameters["brand"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing brand")
                val vehicles = ProviderUseCase.getVehiclesByBrand(brand)
                call.respond(vehicles)
            }

            post {
                val vehicle = call.receive<Vehicle>()
                if (ProviderUseCase.insertVehicle(vehicle)) {
                    call.respond(HttpStatusCode.Created, "Vehicle created")
                } else {
                    call.respond(HttpStatusCode.Conflict, "Vehicle already exists")
                }
            }

            put("/{id}") {
                val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing id")
                val updateVehicle = call.receive<UpdateVehicle>()
                if (ProviderUseCase.updateVehicle(updateVehicle, id)) {
                    call.respond(HttpStatusCode.OK, "Vehicle updated")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Vehicle not found")
                }
            }

            delete("/{id}") {
                val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing id")
                if (ProviderUseCase.deleteVehicle(id)) {
                    call.respond(HttpStatusCode.OK, "Vehicle deleted")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Vehicle not found")
                }
            }
        }
    }
}
