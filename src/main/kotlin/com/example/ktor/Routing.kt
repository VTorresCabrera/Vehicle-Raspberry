package com.example.ktor

import com.example.data.persistence.models.FavoriteTable
import com.example.data.persistence.models.VehicleTable
import com.example.data.persistence.models.UserTable
import com.example.data.persistence.models.VehicleDao
import com.example.data.persistence.models.suspendTransaction
import com.example.domain.mapping.VehicleDaoToVehicle
import com.example.domain.models.*
import com.example.domain.usecase.ProviderUseCase
import com.example.domain.security.JwtConfig
import io.ktor.http.*
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.jvm.javaio.toInputStream
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.mindrot.jbcrypt.BCrypt
import java.io.File
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq

private fun User.toApiResponse(): ApiUserResponse = ApiUserResponse(
    id = this.id,
    username = this.username,
    email = this.email,
    token = this.token ?: "",
    description = this.description,
    phone = this.phone,
    urlImage = this.urlImage
)

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Vehicle API is running!")
        }

        get("/health") {
            call.respond(mapOf("status" to "ok"))
        }

        // Static plugin
        staticResources("/static", "static")

        // Serve uploaded files
        staticFiles("/upload", File("upload"))

        // Global Vehicles (Marketplace)
        route("/vehicles") {
            get {
                val q = call.request.queryParameters["q"]?.trim()?.takeIf { it.isNotEmpty() }
                val brand = call.request.queryParameters["brand"]?.trim()?.takeIf { it.isNotEmpty() }
                val status = call.request.queryParameters["status"]?.trim()?.takeIf { it.isNotEmpty() }
                val minPrice = call.request.queryParameters["minPrice"]?.toDoubleOrNull()
                val maxPrice = call.request.queryParameters["maxPrice"]?.toDoubleOrNull()

                var vehicles = ProviderUseCase.getAllVehicles()

                if (q != null) {
                    vehicles = vehicles.filter {
                        it.marca.contains(q, ignoreCase = true) || it.modelo.contains(q, ignoreCase = true)
                    }
                }
                if (brand != null) {
                    vehicles = vehicles.filter { it.marca.contains(brand, ignoreCase = true) }
                }
                if (status != null) {
                    vehicles = vehicles.filter { (it.status).contains(status, ignoreCase = true) }
                }
                if (minPrice != null) {
                    vehicles = vehicles.filter { it.precio >= minPrice }
                }
                if (maxPrice != null) {
                    vehicles = vehicles.filter { it.precio <= maxPrice }
                }

                call.respond(vehicles)
            }

            // legacy route
            get("/brand/{brand}") {
                val brand = call.parameters["brand"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                val vehicles = ProviderUseCase.getAllVehicles().filter {
                    it.marca.contains(brand, ignoreCase = true)
                }
                call.respond(vehicles)
            }
        }

        get("/vehicle/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val vehicle = ProviderUseCase.getVehicleById(id)
            if (vehicle != null) call.respond(vehicle) else call.respond(HttpStatusCode.NotFound)
        }

        // LOGIN
        post("/login") {
            try {
                val loginRequest = call.receive<LoginRequest>()
                val email = loginRequest.email
                val password = loginRequest.password

                val user = ProviderUseCase.getUserByEmail(email)
                if (user == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
                    return@post
                }

                if (!BCrypt.checkpw(password, user.password)) {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
                    return@post
                }

                val token = JwtConfig.generateToken(user.email)
                ProviderUseCase.updateUser(UpdateUser(token = token), user.id)

                val updated = ProviderUseCase.getUserById(user.id) ?: user.copy(token = token)
                call.respond(updated.toApiResponse())
            } catch (e: Exception) {
                call.application.environment.log.error("Login error: ${e.message}", e)
                call.respond(HttpStatusCode.BadRequest, "Invalid request body")
            }
        }

        // REGISTER
        post("/register") {
            try {
                val req = call.receive<RegisterRequest>()

                if (ProviderUseCase.getUserByEmail(req.email) != null) {
                    call.respond(HttpStatusCode.Conflict, "Email already exists")
                    return@post
                }

                val token = JwtConfig.generateToken(req.email)

                val user = User(
                    id = req.id,
                    username = req.username,
                    email = req.email,
                    password = req.password,
                    description = req.description ?: "",
                    phone = req.phone ?: "",
                    urlImage = req.urlImage,
                    token = token,
                    active = true,
                    role = Role.USER
                )

                if (ProviderUseCase.insertUser(user)) {
                    call.respond(HttpStatusCode.Created, user.toApiResponse())
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to register")
                }
            } catch (e: Exception) {
                call.application.environment.log.error("Register error: ${e.message}", e)
                call.respond(HttpStatusCode.BadRequest, "Invalid register data")
            }
        }

        // UPLOAD PROFILE IMAGE
        post("/upload/{id}") {
            val userId = call.parameters["id"] ?: return@post call.respond(HttpStatusCode.BadRequest, "ID missing")
            val user = ProviderUseCase.getUserById(userId) ?: return@post call.respond(HttpStatusCode.NotFound, "User not found")

            var imageUrl = ""
            val uploadDir = File("upload/images/$userId")
            if (!uploadDir.exists()) uploadDir.mkdirs()

            try {
                val multipart = call.receiveMultipart()
                multipart.forEachPart { part ->
                    if (part is PartData.FileItem) {
                        val originalName = part.originalFileName ?: "image.jpg"
                        val ext = File(originalName).extension.takeIf { it.isNotEmpty() } ?: "jpg"
                        val fileName = "profile_${System.currentTimeMillis()}.$ext"
                        val file = File(uploadDir, fileName)

                        part.provider().toInputStream().use { input ->
                            file.outputStream().buffered().use { output ->
                                input.copyTo(output)
                            }
                        }
                        imageUrl = "/upload/images/$userId/$fileName"
                    }
                    part.dispose()
                }

                if (imageUrl.isNotEmpty()) {
                    ProviderUseCase.updateUser(UpdateUser(urlImage = imageUrl), userId)
                    call.respond(HttpStatusCode.OK, UploadImageResponse(url = imageUrl))
                } else {
                    call.respond(HttpStatusCode.BadRequest, "No file uploaded")
                }
            } catch (e: Exception) {
                call.application.environment.log.error("Upload error: ${e.message}", e)
                call.respond(HttpStatusCode.InternalServerError, "Error processing upload")
            }
        }

        authenticate("auth-jwt") {
            route("/users") {
                get {
                    call.respond(ProviderUseCase.getAllUsers().map { it.toApiResponse() })
                }

                get("/{id}") {
                    val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val user = ProviderUseCase.getUserById(id) ?: return@get call.respond(HttpStatusCode.NotFound)
                    call.respond(user.toApiResponse())
                }

                put("/{id}") {
                    val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
                    val update = call.receive<UpdateUser>()

                    // If password is updated, store it hashed.
                    val safeUpdate = if (update.password != null) {
                        update.copy(password = BCrypt.hashpw(update.password, BCrypt.gensalt()))
                    } else update

                    val ok = ProviderUseCase.updateUser(safeUpdate, id)
                    if (!ok) {
                        call.respond(HttpStatusCode.NotFound)
                        return@put
                    }

                    val updated = ProviderUseCase.getUserById(id) ?: return@put call.respond(HttpStatusCode.NotFound)
                    call.respond(HttpStatusCode.OK, updated.toApiResponse())
                }

                delete("/{id}") {
                    val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
                    if (ProviderUseCase.deleteUser(id)) {
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }

                // Vehicles per User
                route("/{userId}/vehicles") {
                    get {
                        val userId = call.parameters["userId"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                        val vehicles = ProviderUseCase.getVehiclesByUserId(userId)
                        call.respond(vehicles)
                    }

                    post {
                        val userId = call.parameters["userId"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                        try {
                            val vehicle = call.receive<Vehicle>().copy(userId = userId)
                            if (ProviderUseCase.insertVehicle(vehicle)) {
                                call.respond(HttpStatusCode.Created)
                            } else {
                                call.respond(HttpStatusCode.Conflict)
                            }
                        } catch (e: Exception) {
                            call.respond(HttpStatusCode.BadRequest, "Invalid Vehicle data: ${e.message}")
                        }
                    }

                    delete {
                        val userId = call.parameters["userId"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
                        if (ProviderUseCase.deleteVehiclesByUserId(userId)) {
                            call.respond(HttpStatusCode.OK)
                        } else {
                            call.respond(HttpStatusCode.NotFound)
                        }
                    }

                    // Single Vehicle operations under User
                    route("/{vehicleId}") {
                        get {
                            val userId = call.parameters["userId"]!!
                            val vehicleId = call.parameters["vehicleId"]!!
                            val vehicle = ProviderUseCase.getVehicleById(vehicleId)
                            if (vehicle != null && vehicle.userId == userId) call.respond(vehicle)
                            else call.respond(HttpStatusCode.NotFound)
                        }

                        put {
                            val userId = call.parameters["userId"]!!
                            val vehicleId = call.parameters["vehicleId"]!!
                            val update = call.receive<UpdateVehicle>()

                            val existing = ProviderUseCase.getVehicleById(vehicleId)
                            if (existing == null || existing.userId != userId) {
                                call.respond(HttpStatusCode.NotFound)
                                return@put
                            }

                            if (ProviderUseCase.updateVehicle(update, vehicleId)) {
                                call.respond(HttpStatusCode.OK)
                            } else {
                                call.respond(HttpStatusCode.InternalServerError)
                            }
                        }

                        delete {
                            val userId = call.parameters["userId"]!!
                            val vehicleId = call.parameters["vehicleId"]!!

                            val existing = ProviderUseCase.getVehicleById(vehicleId)
                            if (existing == null || existing.userId != userId) {
                                call.respond(HttpStatusCode.NotFound)
                                return@delete
                            }

                            if (ProviderUseCase.deleteVehicle(vehicleId)) {
                                call.respond(HttpStatusCode.OK)
                            } else {
                                call.respond(HttpStatusCode.InternalServerError)
                            }
                        }

                        // Upload vehicle image
                        post("/upload") {
                            val userId = call.parameters["userId"]!!
                            val vehicleId = call.parameters["vehicleId"]!!

                            val existing = ProviderUseCase.getVehicleById(vehicleId)
                            if (existing == null || existing.userId != userId) {
                                call.respond(HttpStatusCode.NotFound)
                                return@post
                            }

                            var imageUrl = ""
                            val uploadDir = File("upload/vehicles/$vehicleId")
                            if (!uploadDir.exists()) uploadDir.mkdirs()

                            try {
                                val multipart = call.receiveMultipart()
                                multipart.forEachPart { part ->
                                    if (part is PartData.FileItem) {
                                        val originalName = part.originalFileName ?: "image.jpg"
                                        val ext = File(originalName).extension.takeIf { it.isNotEmpty() } ?: "jpg"
                                        val fileName = "vehicle_${System.currentTimeMillis()}.$ext"
                                        val file = File(uploadDir, fileName)

                                        part.provider().toInputStream().use { input ->
                                            file.outputStream().buffered().use { output ->
                                                input.copyTo(output)
                                            }
                                        }

                                        imageUrl = "/upload/vehicles/$vehicleId/$fileName"
                                    }
                                    part.dispose()
                                }

                                if (imageUrl.isBlank()) {
                                    call.respond(HttpStatusCode.BadRequest, "No file uploaded")
                                    return@post
                                }

                                ProviderUseCase.updateVehicle(UpdateVehicle(imagen = imageUrl), vehicleId)
                                call.respond(HttpStatusCode.OK, UploadImageResponse(url = imageUrl))
                            } catch (e: Exception) {
                                call.application.environment.log.error("Upload vehicle error: ${e.message}", e)
                                call.respond(HttpStatusCode.InternalServerError, "Error processing upload")
                            }
                        }
                    }
                }

                // Favorites
                route("/{userId}/favorites") {
                    get {
                        val userId = call.parameters["userId"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                        val ids = getFavoriteIds(userId)
                        val vehicles = getVehiclesByIds(ids)
                        call.respond(vehicles)
                    }

                    get("/ids") {
                        val userId = call.parameters["userId"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                        call.respond(getFavoriteIds(userId))
                    }

                    post("/{vehicleId}") {
                        val userId = call.parameters["userId"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                        val vehicleId = call.parameters["vehicleId"] ?: return@post call.respond(HttpStatusCode.BadRequest)

                        val ok = addFavorite(userId, vehicleId)
                        if (ok) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.Conflict)
                    }

                    delete("/{vehicleId}") {
                        val userId = call.parameters["userId"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
                        val vehicleId = call.parameters["vehicleId"] ?: return@delete call.respond(HttpStatusCode.BadRequest)

                        val ok = removeFavorite(userId, vehicleId)
                        if (ok) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.NotFound)
                    }
                }
            }
        }
    }
}

private suspend fun getFavoriteIds(userId: String): List<String> = suspendTransaction {
    FavoriteTable
        .selectAll().where { FavoriteTable.user eq EntityID(userId, UserTable) }
        .map { it[FavoriteTable.vehicle].value }
}

private suspend fun addFavorite(userId: String, vehicleId: String): Boolean = suspendTransaction {
    try {
        FavoriteTable.insert {
            it[user] = EntityID(userId, UserTable)
            it[vehicle] = EntityID(vehicleId, VehicleTable)
            it[createdAt] = System.currentTimeMillis()
        }
        true
    } catch (_: Exception) {
        false
    }
}

private suspend fun removeFavorite(userId: String, vehicleId: String): Boolean = suspendTransaction {
    FavoriteTable.deleteWhere {
        (FavoriteTable.user eq EntityID(userId, UserTable)) and (FavoriteTable.vehicle eq EntityID(vehicleId, VehicleTable))
    } > 0
}

private suspend fun getVehiclesByIds(ids: List<String>): List<Vehicle> = suspendTransaction {
    if (ids.isEmpty()) return@suspendTransaction emptyList()

    // Using DAO keeps mapping consistent.
    val entityIds = ids.map { EntityID(it, VehicleTable) }
    VehicleDao.find { VehicleTable.id inList entityIds }
        .map(::VehicleDaoToVehicle)
}
