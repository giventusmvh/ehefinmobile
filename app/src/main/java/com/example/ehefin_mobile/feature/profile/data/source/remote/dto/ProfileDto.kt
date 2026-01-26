package com.example.ehefin_mobile.feature.profile.data.source.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Response DTO for GET profile endpoint.
 * The profile data is nested inside a 'profile' object.
 */
data class ProfileResponseDto(
        @SerializedName("id") val id: Long,
        @SerializedName("name") val name: String,
        @SerializedName("email") val email: String,
        @SerializedName("userType") val userType: String?,
        @SerializedName("isActive") val isActive: Boolean?,
        @SerializedName("profile") val profile: ProfileDataDto?,
        @SerializedName("roles") val roles: List<String>?
)

/**
 * Nested profile data DTO
 */
data class ProfileDataDto(
        @SerializedName("id") val id: Long?,
        @SerializedName("nik") val nik: String?,
        @SerializedName("birthdate") val birthdate: String?,
        @SerializedName("phone") val phone: String?,
        @SerializedName("address") val address: String?,
        @SerializedName("ktpUrl") val ktpUrl: String?,
        @SerializedName("kkUrl") val kkUrl: String?,
        @SerializedName("npwpUrl") val npwpUrl: String?,
        @SerializedName("bankName") val bankName: String?,
        @SerializedName("accountNumber") val accountNumber: String?,
        @SerializedName("accountHolderName") val accountHolderName: String?,
        @SerializedName("isComplete") val isComplete: Boolean?
)

data class UpdateProfileRequest(
        @SerializedName("nik") val nik: String,
        @SerializedName("phone") val phone: String,
        @SerializedName("address") val address: String,
        @SerializedName("bankName") val bankName: String,
        @SerializedName("accountNumber") val accountNumber: String,
        @SerializedName("accountHolderName") val accountHolderName: String,
        @SerializedName("birthdate") val birthdate: String
)

/**
 * Response DTO for update profile endpoint. The update profile endpoint returns a different
 * structure than get profile.
 */
data class UpdateProfileResponseDto(
        @SerializedName("nik") val nik: String?,
        @SerializedName("birthdate") val birthdate: String?,
        @SerializedName("phoneNumber") val phoneNumber: String?,
        @SerializedName("address") val address: String?,
        @SerializedName("ktpUrl") val ktpUrl: String?,
        @SerializedName("kkUrl") val kkUrl: String?,
        @SerializedName("npwpUrl") val npwpUrl: String?,
        @SerializedName("bankName") val bankName: String?,
        @SerializedName("accountNumber") val accountNumber: String?,
        @SerializedName("accountHolderName") val accountHolderName: String?,
        @SerializedName("isComplete") val isComplete: Boolean?
)

data class FcmTokenRequest(@SerializedName("fcmToken") val fcmToken: String)
