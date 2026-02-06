package com.example.ehefin_mobile.feature.profile.data.source.remote

import com.example.ehefin_mobile.core.common.DataResult
import com.example.ehefin_mobile.feature.profile.data.source.remote.dto.ProfileResponseDto
import com.example.ehefin_mobile.feature.profile.data.source.remote.dto.UpdateProfileResponseDto
import java.io.File

/**
 * Interface for Profile remote data source operations.
 * Abstracts API calls for testability and separation of concerns.
 */
interface ProfileRemoteDataSource {
    
    /**
     * Fetch user profile from remote API.
     */
    suspend fun getProfile(): DataResult<ProfileResponseDto>
    
    /**
     * Update user profile with optional document uploads.
     */
    suspend fun updateProfile(
        nik: String,
        phoneNumber: String,
        address: String,
        bankName: String,
        accountNumber: String,
        accountHolderName: String,
        birthdate: String,
        job: String,
        companyName: String,
        ktpFile: File? = null,
        kkFile: File? = null,
        npwpFile: File? = null,
        selfieFile: File? = null,
        salarySlipFile: File? = null
    ): DataResult<UpdateProfileResponseDto>
    
    /**
     * Register FCM token for push notifications.
     */
    suspend fun registerFcmToken(token: String): DataResult<Unit>
}