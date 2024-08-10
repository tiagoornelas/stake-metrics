package net.stakemetrics.http.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import java.util.Date
import java.util.concurrent.TimeUnit
import net.stakemetrics.application.entities.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service


@Service
class SecurityTokenService {

    @Value("\${api.security.token.secret}")
    private val secretKey: String = "secretKey"
    private val issuer: String = "back-end"
    private val expirationTime = Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(3))
    private val algorithm = Algorithm.HMAC256(secretKey)

    fun generateToken(user: User): String {
        try {
            val jwtCreator = JWT.create()
                .withIssuer(issuer)
                .withSubject(user.email)

            jwtCreator.withExpiresAt(expirationTime)

            return jwtCreator.sign(algorithm)
        } catch (e: JWTCreationException) {
            throw JWTCreationException("Error occurred while generating JWT token", e)
        }
    }

    fun validateToken(token: String): DecodedJWT {
        try {
            return JWT.require(algorithm).withIssuer(issuer).build().verify(token)
        } catch (e: JWTVerificationException) {
            throw JWTVerificationException("Validation error on provided JWT Token", e)
        }
    }
}