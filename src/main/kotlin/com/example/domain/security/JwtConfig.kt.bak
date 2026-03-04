package com.example.domain.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.auth.jwt.*
import java.sql.Date
/*
CON ESTE OBJECT, KTOR APRENDE A CREAR EL TOKEN Y A VERIFICARLO/VALIDARLO

Sabemos que el token lleva un payload con los datos que después comentaremos y sabemos que
se pueden leer sin problema en base64, por tanto el token no está encriptado. Lo que hace el
protocolo, es utilizar una clave secreta, para verificar si el token ha sido alterado o modificado.
Por ejemplo, cualquier que intente modificar el username que encierra el payload o la fecha de expiración,
al verificar su autentidad con la clave secreta, verá que ha sido alterado y por tanto la firma digital se rompe
con la NO aceptacón del token por parte del cliente.

1.- secret lo utiliza el algoritmo para firmar el token. Debe ser unica y secreta aquí. Garantiza la autenticidad.
2.- issuer lo utilizamos para identificar el dominio de la api.
3.- audience lo utiliza el servidor, para marcar qué cliente es el que solicita los servicios.
4.- realm lo utiliza, para indicar un comentario de respuesta para aquellas solicitudes que
bien solicitan un endpoint protegido sin token, o símplemente el token no es correcto. Le devolvería
información de que es importante un contexto de autenticación.

El flujo sería:
1.- El cliente se loguea con sus credenciales (usuario y password) y para ello el servidor le proporcina un token válido en el caso de que sea logueado correctamente.
2.- En el token, la api encapsula datos como el dominio, la audiencia, el contexto de autenticación, más otros datos como la fecha de expiración
 y algunos datos personalizados como el username/dni y utiliza
la clave secreta para firmar dicho token. Se genera, gracias al algoritmo de encirptación SHAC256.
3.- Dicho token se genera gracias al algoritmo de encriptación y se le envía al cliente.
4.- El cliente recibe el token, lo almacena y por cada enpoint, debe de mandarlo.  Authorization: Bearer <TOKEN>
5.- El servidor, recibe el token y lo valida con la clave secret.
   - verifica la fecha de expiración
   - verifica el issuer, audience.
   - extrae información encapsulada como (dni/username)
6.- Si el token es válido, responderá con el endpoint solicitado. En caso contrario, se mandará una response
con **401 Unauthorized** y un header `WWW-Authenticate` con el `realm`.

 */
object JwtConfig {
    private const val secret = "super_secret_key"  // 🔑 Cambia esto por algo más seguro
    private const val issuer = "domain.com"
    private const val audience = "ktor_audience"
    private const val realm = "ktor_realm"
    private val algorithm = Algorithm.HMAC256(secret)

    /*
    No quiero que expire. De lo contrario necesitaría un segundo token de refresco.
     */
    fun generateToken(email: String): String {
        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withSubject("Authentication")
            .withClaim("email", email) //son específicas del usuario.
            .withClaim("time", System.currentTimeMillis()) //específicas del usuario.
           // .withExpiresAt(Date(System.currentTimeMillis() + 600000))  // Expira en 10 min
            .sign(algorithm)
    }

    /*
    Se encargará de la verificación del token.
    1.- Se recibe un token y a partir del contexto de autenticación.
    2.- Se comprueba la validación del mismo utilizando verifier.
        - pasamos el algoritmo donde se define la clave secreta.
        - pasamos el dominio y la audience
        Si el issueer y la audience coincide con lo configurado, se acepta el token.
    3.- Si se acepta el token, ahora toca validarlo y extraer información.
       - credential.payload (contiene la información del payload en base64)
          - Extraemos el username y comprobamos que sea distinto de null. Si existe, se crea
          un objeto JWTPrincipal con la información del payload. esto sería valido:
          {
               "username": "john_doe"
          }
    4.- Se crea un objeto JWTPrincipal, con información del usuario extraída del token
     */
    fun configureAuthentication(config: JWTAuthenticationProvider.Config) {
        config.realm = realm
        config.verifier(
            JWT.require(algorithm)
                .withIssuer(issuer)
                .withAudience(audience)
                .build()
        )
        config.validate { credential ->
            if (credential.payload.getClaim("email").asString() != null) {
                JWTPrincipal(credential.payload)
            } else null
        }
    }
}