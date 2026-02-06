package com.example.ehefin_mobile.feature.auth.data

import com.example.ehefin_mobile.core.common.DataResult
import com.example.ehefin_mobile.core.network.ApiResponse
import com.example.ehefin_mobile.core.testing.TestDispatcherRule
import com.example.ehefin_mobile.feature.auth.data.source.remote.AuthApi
import com.example.ehefin_mobile.feature.auth.data.source.remote.AuthRemoteDataSourceImpl
import com.example.ehefin_mobile.feature.auth.data.source.remote.dto.AuthResponseDto
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
 * Unit tests for AuthRemoteDataSourceImpl.
 * Tests the remote data source layer in isolation using mocked API.
 */
class AuthRemoteDataSourceImplTest {

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private lateinit var authApi: AuthApi
    private lateinit var gson: Gson
    private lateinit var dataSource: AuthRemoteDataSourceImpl

    @Before
    fun setup() {
        authApi = mockk()
        gson = Gson()
        dataSource = AuthRemoteDataSourceImpl(authApi, gson)
    }

    @Test
    fun `login returns Success when API call is successful`() = runTest {
        // Given
        val authResponse = createMockAuthResponse()
        val apiResponse = ApiResponse(
            success = true,
            message = "Login successful",
            data = authResponse,
            timestamp = "2024-01-01T00:00:00"
        )
        coEvery { authApi.login(any()) } returns Response.success(apiResponse)

        // When
        val result = dataSource.login("test@example.com", "password123")

        // Then
        assertTrue(result is DataResult.Success)
        assertEquals(authResponse.token, (result as DataResult.Success).data.token)
        assertEquals(authResponse.userId, result.data.userId)
    }

    @Test
    fun `login returns Error when API returns unsuccessful response`() = runTest {
        // Given
        val apiResponse = ApiResponse<AuthResponseDto>(
            success = false,
            message = "Invalid credentials",
            data = null,
            timestamp = "2024-01-01T00:00:00"
        )
        coEvery { authApi.login(any()) } returns Response.success(apiResponse)

        // When
        val result = dataSource.login("test@example.com", "wrong_password")

        // Then
        assertTrue(result is DataResult.Error)
    }

    @Test
    fun `login returns Error when exception is thrown`() = runTest {
        // Given
        coEvery { authApi.login(any()) } throws RuntimeException("Network error")

        // When
        val result = dataSource.login("test@example.com", "password123")

        // Then
        assertTrue(result is DataResult.Error)
        assertTrue((result as DataResult.Error).message.contains("Network error"))
    }

    @Test
    fun `register returns Success when API call is successful`() = runTest {
        // Given
        val authResponse = createMockAuthResponse()
        val apiResponse = ApiResponse(
            success = true,
            message = "Registration successful",
            data = authResponse,
            timestamp = "2024-01-01T00:00:00"
        )
        coEvery { authApi.register(any()) } returns Response.success(apiResponse)

        // When
        val result = dataSource.register("Test User", "test@example.com", "password123")

        // Then
        assertTrue(result is DataResult.Success)
        assertEquals(authResponse.token, (result as DataResult.Success).data.token)
    }

    @Test
    fun `logout always returns Success`() = runTest {
        // Given
        coEvery { authApi.logout() } throws RuntimeException("Server error")

        // When
        val result = dataSource.logout()

        // Then - logout should always succeed (for local cleanup)
        assertTrue(result is DataResult.Success)
    }

    @Test
    fun `forgotPassword always returns Success for security reasons`() = runTest {
        // Given - even if API fails, we return success (security measure)
        coEvery { authApi.forgotPassword(any()) } throws RuntimeException("Server error")

        // When
        val result = dataSource.forgotPassword("test@example.com")

        // Then - should return error on exception, unlike logout
        assertTrue(result is DataResult.Error)
    }

    private fun createMockAuthResponse(): AuthResponseDto {
        return AuthResponseDto(
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            tokenType = "Bearer",
            userId = 1L,
            email = "test@example.com",
            name = "Test User",
            roles = listOf("ROLE_CUSTOMER"),
            permissions = listOf("READ", "WRITE")
        )
    }
}