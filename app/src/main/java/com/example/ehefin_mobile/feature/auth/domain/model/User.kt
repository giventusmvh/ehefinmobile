package com.example.ehefin_mobile.feature.auth.domain.model

/**
 * Domain model for authenticated user
 */
data class User(
    val id: Long,
    val email: String,
    val name: String,
    val roles: List<String>,
    val permissions: List<String>
)

/**
 * Result of authentication operations
 */
data class AuthResult(
    val token: String,
    val tokenType: String,
    val userId: Long,
    val email: String,
    val name: String,
    val roles: List<String>,
    val permissions: List<String>
)