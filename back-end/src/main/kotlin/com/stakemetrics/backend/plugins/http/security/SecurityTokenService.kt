package com.stakemetrics.backend.plugins.http.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.stakemetrics.backend.domain.enums.UserTypes
import com.stakemetrics.backend.plugins.persistence.models.UserModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.TimeUnit


@Service
class SecurityTokenService {

    @Value("\${api.security.token.secret}")
    private val secretKey: String = "secretKey"
    private val issuer: String = "back-end"
    private val expirationTime = Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(3))
    private val algorithm = Algorithm.HMAC256(secretKey)

    fun generateToken(user: UserModel): String {
        try {
        val jwtCreator = JWT.create()
            .withIssuer(issuer)
            .withSubject(user.email)

        if (user.type != UserTypes.SERVICE) {
            jwtCreator.withExpiresAt(expirationTime)
        }

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