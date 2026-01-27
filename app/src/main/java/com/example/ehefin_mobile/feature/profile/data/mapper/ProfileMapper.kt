package com.example.ehefin_mobile.feature.profile.data.mapper

import com.example.ehefin_mobile.feature.profile.data.source.local.ProfileEntity
import com.example.ehefin_mobile.feature.profile.data.source.remote.dto.ProfileResponseDto
import com.example.ehefin_mobile.feature.profile.data.source.remote.dto.UpdateProfileResponseDto
import com.example.ehefin_mobile.feature.profile.domain.model.UserProfile

fun ProfileResponseDto.toEntity(): ProfileEntity {
    return ProfileEntity(
            userId = id,
            name = name,
            email = email,
            nik = profile?.nik,
            birthdate = profile?.birthdate,
            phoneNumber = profile?.phone,
            address = profile?.address,
            ktpPath = profile?.ktpUrl?.let { sanitizeUrl(it) },
            kkPath = profile?.kkUrl?.let { sanitizeUrl(it) },
            npwpPath = profile?.npwpUrl?.let { sanitizeUrl(it) },
            bankName = profile?.bankName,
            accountNumber = profile?.accountNumber,
            accountHolderName = profile?.accountHolderName,
            isComplete = profile?.isComplete ?: false
    )
}

fun ProfileEntity.toDomain(): UserProfile {
    return UserProfile(
            userId = userId,
            name = name,
            email = email,
            nik = nik,
            birthdate = birthdate,
            phoneNumber = phoneNumber,
            address = address,
            ktpPath = ktpPath,
            kkPath = kkPath,
            npwpPath = npwpPath,
            bankName = bankName,
            accountNumber = accountNumber,
            accountHolderName = accountHolderName,
            isComplete = isComplete
    )
}

fun ProfileResponseDto.toDomain(): UserProfile {
    return toEntity().toDomain()
}

/**
 * Convert UpdateProfileResponseDto to UserProfile domain model. Note: Update profile response
 * doesn't include userId, name, email - these need to be provided from existing profile data.
 */
fun UpdateProfileResponseDto.toDomain(
        existingUserId: Long,
        existingName: String,
        existingEmail: String
): UserProfile {
    return UserProfile(
            userId = existingUserId,
            name = existingName,
            email = existingEmail,
            nik = nik,
            birthdate = birthdate,
            phoneNumber = phoneNumber,
            address = address,
            ktpPath = ktpUrl?.let { sanitizeUrl(it) },
            kkPath = kkUrl?.let { sanitizeUrl(it) },
            npwpPath = npwpUrl?.let { sanitizeUrl(it) },
            bankName = bankName,
            accountNumber = accountNumber,
            accountHolderName = accountHolderName,
            isComplete = isComplete ?: false
    )
}

private fun sanitizeUrl(url: String): String {
    // Handle Postman variable placeholder
    var sanitized = url.replace("{{baseURL}}", "http://10.0.2.2:8080")
    
    // Handle localhost for emulator
    sanitized = sanitized.replace("localhost", "10.0.2.2")
    
    // Handle relative paths
    if (sanitized.startsWith("/")) {
        sanitized = "http://10.0.2.2:8080$sanitized"
    } else if (sanitized.startsWith("uploads/")) {
        sanitized = "http://10.0.2.2:8080/$sanitized"
    }
    
    return sanitized
}
