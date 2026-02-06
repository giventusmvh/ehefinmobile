package com.example.ehefin_mobile.feature.profile.data

import com.example.ehefin_mobile.core.common.DataResult
import com.example.ehefin_mobile.feature.profile.data.source.remote.ProfileRemoteDataSource
import com.example.ehefin_mobile.feature.profile.data.source.remote.dto.ProfileDataDto
import com.example.ehefin_mobile.feature.profile.data.source.remote.dto.ProfileResponseDto
import com.example.ehefin_mobile.feature.profile.data.source.remote.dto.UpdateProfileResponseDto
import java.io.File

/**
 * Fake implementation of ProfileRemoteDataSource for testing.
 * Allows configuring responses for each method.
 */
class FakeProfileRemoteDataSource : ProfileRemoteDataSource {

    var profileResult: DataResult<ProfileResponseDto> = DataResult.Success(createMockProfileResponse())
    var updateProfileResult: DataResult<UpdateProfileResponseDto> = DataResult.Success(createMockUpdateResponse())
    var registerFcmTokenResult: DataResult<Unit> = DataResult.Success(Unit)

    var getProfileCallCount = 0
    var updateProfileCallCount = 0
    var registerFcmTokenCallCount = 0

    override suspend fun getProfile(): DataResult<ProfileResponseDto> {
        getProfileCallCount++
        return profileResult
    }

    override suspend fun updateProfile(
        nik: String,
        phoneNumber: String,
        address: String,
        bankName: String,
        accountNumber: String,
        accountHolderName: String,
        birthdate: String,
        job: String,
        companyName: String,
        ktpFile: File?,
        kkFile: File?,
        npwpFile: File?,
        selfieFile: File?,
        salarySlipFile: File?
    ): DataResult<UpdateProfileResponseDto> {
        updateProfileCallCount++
        return updateProfileResult
    }

    override suspend fun registerFcmToken(token: String): DataResult<Unit> {
        registerFcmTokenCallCount++
        return registerFcmTokenResult
    }

    fun reset() {
        profileResult = DataResult.Success(createMockProfileResponse())
        updateProfileResult = DataResult.Success(createMockUpdateResponse())
        registerFcmTokenResult = DataResult.Success(Unit)
        getProfileCallCount = 0
        updateProfileCallCount = 0
        registerFcmTokenCallCount = 0
    }

    companion object {
        fun createMockProfileResponse(): ProfileResponseDto {
            return ProfileResponseDto(
                id = 1L,
                name = "Test User",
                email = "test@example.com",
                userType = "CUSTOMER",
                isActive = true,
                profile = ProfileDataDto(
                    id = 1L,
                    nik = "1234567890123456",
                    birthdate = "1990-01-01",
                    phone = "08123456789",
                    address = "Test Address",
                    ktpUrl = null,
                    kkUrl = null,
                    npwpUrl = null,
                    bankName = "Bank Test",
                    accountNumber = "1234567890",
                    accountHolderName = "Test User",
                    job = "Software Engineer",
                    companyName = "Test Company",
                    selfieUrl = null,
                    salarySlipUrl = null,
                    isComplete = true
                ),
                roles = listOf("ROLE_CUSTOMER")
            )
        }

        fun createMockUpdateResponse(): UpdateProfileResponseDto {
            return UpdateProfileResponseDto(
                nik = "1234567890123456",
                birthdate = "1990-01-01",
                phoneNumber = "08123456789",
                address = "Test Address",
                ktpUrl = null,
                kkUrl = null,
                npwpUrl = null,
                bankName = "Bank Test",
                accountNumber = "1234567890",
                accountHolderName = "Test User",
                job = "Software Engineer",
                companyName = "Test Company",
                selfieUrl = null,
                salarySlipUrl = null,
                isComplete = true
            )
        }
    }
}