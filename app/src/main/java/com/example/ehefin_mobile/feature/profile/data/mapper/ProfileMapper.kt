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
            ktpPath = profile?.ktpUrl,
            kkPath = profile?.kkUrl,
            npwpPath = profile?.npwpUrl,
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
            ktpPath = ktpUrl,
            kkPath = kkUrl,
            npwpPath = npwpUrl,
            bankName = bankName,
            accountNumber = accountNumber,
            accountHolderName = accountHolderName,
            isComplete = isComplete ?: false
    )
}
