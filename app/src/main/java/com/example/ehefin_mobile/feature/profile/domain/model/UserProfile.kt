package com.example.ehefin_mobile.feature.profile.domain.model

/**
 * Domain model for User Profile
 */
data class UserProfile(
    val userId: Long,
    val name: String,
    val email: String,
    val nik: String?,
    val birthdate: String?,
    val phoneNumber: String?,
    val address: String?,
    val ktpPath: String?,
    val kkPath: String?,
    val npwpPath: String?,
    val bankName: String?,
    val accountNumber: String?,
    val accountHolderName: String?,
    val isComplete: Boolean
)
