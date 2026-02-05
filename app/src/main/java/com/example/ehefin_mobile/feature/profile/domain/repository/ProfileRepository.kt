package com.example.ehefin_mobile.feature.profile.domain.repository

import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.feature.profile.domain.model.UserProfile
import java.io.File
import kotlinx.coroutines.flow.Flow

/** Repository interface for Profile operations (DIP) */
interface ProfileRepository {

        /** Get user profile (offline-first) */
        fun getProfile(): Flow<Resource<UserProfile>>

        /** Update user profile with optional document uploads */
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
                salarySlipFile: File? = null,

        ): Resource<UserProfile>

        /** Register FCM Token */
        suspend fun registerFcmToken(token: String): Resource<Unit>

        /** Refresh profile from remote */
        suspend fun refreshProfile(): Resource<Unit>
}