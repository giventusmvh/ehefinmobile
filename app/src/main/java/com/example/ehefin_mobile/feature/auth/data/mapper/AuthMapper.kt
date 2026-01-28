package com.example.ehefin_mobile.feature.auth.data.mapper

import com.example.ehefin_mobile.feature.auth.data.source.remote.dto.AuthResponseDto
import com.example.ehefin_mobile.feature.auth.domain.model.AuthResult
import com.example.ehefin_mobile.feature.auth.domain.model.User

/**
 * Mapper functions to convert DTOs to Domain models
 */

fun AuthResponseDto.toDomain(): AuthResult {
    return AuthResult(
        token = token,
        tokenType = tokenType,
        userId = userId,
        email = email,
        name = name,
        roles = roles,
        permissions = permissions
    )
}

fun AuthResponseDto.toUser(): User {
    return User(
        id = userId,
        email = email,
        name = name,
        roles = roles,
        permissions = permissions
    )
}