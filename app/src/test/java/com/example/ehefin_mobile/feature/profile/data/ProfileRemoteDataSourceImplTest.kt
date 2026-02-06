package com.example.ehefin_mobile.feature.profile.data

import com.example.ehefin_mobile.core.common.DataResult
import com.example.ehefin_mobile.core.network.ApiResponse
import com.example.ehefin_mobile.core.testing.TestDispatcherRule
import com.example.ehefin_mobile.feature.profile.data.source.remote.ProfileApi
import com.example.ehefin_mobile.feature.profile.data.source.remote.ProfileRemoteDataSourceImpl
import com.example.ehefin_mobile.feature.profile.data.source.remote.dto.ProfileDataDto
import com.example.ehefin_mobile.feature.profile.data.source.remote.dto.ProfileResponseDto
import com.google.gson.Gson
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

/**
 * Unit tests for ProfileRemoteDataSourceImpl.
 * Tests the remote data source layer in isolation using mocked API.
 */
class ProfileRemoteDataSourceImplTest {

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private lateinit var profileApi: ProfileApi
    private lateinit var gson: Gson
    private lateinit var dataSource: ProfileRemoteDataSourceImpl

    @Before
    fun setup() {
        profileApi = mockk()
        gson = Gson()
        dataSource = ProfileRemoteDataSourceImpl(profileApi, gson)
    }

    @Test
    fun `getProfile returns Success when API call is successful`() = runTest {
        // Given
        val profileDto = createMockProfileResponse()
        val apiResponse = ApiResponse(
            success = true,
            message = "Success",
            data = profileDto,
            timestamp = "2024-01-01T00:00:00"
        )
        coEvery { profileApi.getProfile() } returns Response.success(apiResponse)

        // When
        val result = dataSource.getProfile()

        // Then
        assertTrue(result is DataResult.Success)
        assertEquals(profileDto.id, (result as DataResult.Success).data.id)
        assertEquals(profileDto.name, result.data.name)
        assertEquals(profileDto.email, result.data.email)
    }

    @Test
    fun `getProfile returns Error when API returns unsuccessful response`() = runTest {
        // Given
        val apiResponse = ApiResponse<ProfileResponseDto>(
            success = false,
            message = "Unauthorized",
            data = null,
            timestamp = "2024-01-01T00:00:00"
        )
        coEvery { profileApi.getProfile() } returns Response.success(apiResponse)

        // When
        val result = dataSource.getProfile()

        // Then
        assertTrue(result is DataResult.Error)
    }

    @Test
    fun `getProfile returns Error when exception is thrown`() = runTest {
        // Given
        coEvery { profileApi.getProfile() } throws RuntimeException("Network error")

        // When
        val result = dataSource.getProfile()

        // Then
        assertTrue(result is DataResult.Error)
        assertTrue((result as DataResult.Error).message.contains("Network error"))
    }

    @Test
    fun `registerFcmToken returns Success when API call is successful`() = runTest {
        // Given
        val apiResponse = ApiResponse(
            success = true,
            message = "Token registered",
            data = null as Any?,
            timestamp = "2024-01-01T00:00:00"
        )
        @Suppress("UNCHECKED_CAST")
        coEvery { profileApi.registerFcmToken(any()) } returns Response.success(apiResponse) as Response<ApiResponse<Any>>

        // When
        val result = dataSource.registerFcmToken("test_token")

        // Then
        assertTrue(result is DataResult.Success)
    }

    private fun createMockProfileResponse(): ProfileResponseDto {
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
}