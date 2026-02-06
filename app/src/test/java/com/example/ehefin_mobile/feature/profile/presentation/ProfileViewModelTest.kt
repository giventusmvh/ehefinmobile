package com.example.ehefin_mobile.feature.profile.presentation

import app.cash.turbine.test
import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.core.datastore.TokenManager
import com.example.ehefin_mobile.core.testing.TestDispatcherRule
import com.example.ehefin_mobile.feature.profile.domain.model.UserProfile
import com.example.ehefin_mobile.feature.profile.domain.usecase.GetProfileUseCase
import com.example.ehefin_mobile.feature.profile.domain.usecase.UpdateProfileUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for ProfileViewModel.
 * Tests ViewModel layer in isolation using mocked use cases.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private lateinit var getProfileUseCase: GetProfileUseCase
    private lateinit var updateProfileUseCase: UpdateProfileUseCase
    private lateinit var tokenManager: TokenManager
    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setup() {
        getProfileUseCase = mockk()
        updateProfileUseCase = mockk()
        tokenManager = mockk()

        // Default mock for token manager
        every { tokenManager.getAccessToken() } returns flowOf("test_token")
    }

    private fun createViewModel(): ProfileViewModel {
        return ProfileViewModel(getProfileUseCase, updateProfileUseCase, tokenManager)
    }

    private fun createMockProfile(isComplete: Boolean = true): UserProfile {
        return UserProfile(
            userId = 1L,
            name = "Test User",
            email = "test@example.com",
            nik = "1234567890123456",
            birthdate = "1990-01-01",
            phoneNumber = "08123456789",
            address = "Test Address",
            ktpPath = "/ktp.jpg",
            kkPath = "/kk.jpg",
            npwpPath = "/npwp.jpg",
            bankName = "Bank Test",
            accountNumber = "1234567890",
            accountHolderName = "Test User",
            job = "Software Engineer",
            companyName = "Test Company",
            selfiePath = "/selfie.jpg",
            salarySlipPath = "/salary.jpg",
            isComplete = isComplete
        )
    }

    @Test
    fun `initial state is loading with null profile`() = runTest {
        // Given
        every { getProfileUseCase() } returns flowOf(Resource.Loading())

        // When
        viewModel = createViewModel()

        // Then - initial state before any emission
        assertTrue(viewModel.uiState.value.isLoading || viewModel.uiState.value.profile == null)
    }

    @Test
    fun `loadProfile emits Success when use case returns success`() = runTest {
        // Given
        val profile = createMockProfile()
        every { getProfileUseCase() } returns flowOf(
            Resource.Loading(),
            Resource.Success(profile)
        )

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.profile)
        assertEquals("Test User", state.profile?.name)
        assertEquals("test@example.com", state.profile?.email)
        assertNull(state.error)
    }

    @Test
    fun `loadProfile emits Error when use case returns error`() = runTest {
        // Given
        val errorMessage = "Network error"
        every { getProfileUseCase() } returns flowOf(
            Resource.Loading(),
            Resource.Error(errorMessage)
        )

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `loadProfile shows cached data on error if available`() = runTest {
        // Given
        val cachedProfile = createMockProfile()
        every { getProfileUseCase() } returns flowOf(
            Resource.Error("Network error", cachedProfile)
        )

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertNotNull(state.profile) // cached data should be shown
        assertNotNull(state.error) // error message should also be present
    }

    @Test
    fun `onEvent Refresh triggers loadProfile`() = runTest {
        // Given
        val profile = createMockProfile()
        every { getProfileUseCase() } returns flowOf(Resource.Success(profile))

        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.onEvent(ProfileEvent.Refresh)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertNotNull(state.profile)
    }

    @Test
    fun `updateProfile emits Success when use case returns success`() = runTest {
        // Given
        val profile = createMockProfile()
        every { getProfileUseCase() } returns flowOf(Resource.Success(profile))
        coEvery {
            updateProfileUseCase(
                nik = any(),
                phoneNumber = any(),
                address = any(),
                bankName = any(),
                accountNumber = any(),
                accountHolderName = any(),
                birthdate = any(),
                job = any(),
                companyName = any(),
                ktpFile = any(),
                kkFile = any(),
                npwpFile = any(),
                selfieFile = any(),
                salarySlipFile = any()
            )
        } returns Resource.Success(profile)

        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.onEvent(
            ProfileEvent.UpdateProfile(
                nik = "1234567890123456",
                phoneNumber = "08123456789",
                address = "New Address",
                bankName = "Bank New",
                accountNumber = "9876543210",
                accountHolderName = "Test User",
                birthdate = "1990-01-01",
                job = "Developer",
                companyName = "New Company"
            )
        )
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isUpdating)
        assertTrue(state.updateSuccess)
    }

    @Test
    fun `updateProfile emits Error when use case returns error`() = runTest {
        // Given
        val profile = createMockProfile()
        val errorMessage = "Update failed"
        every { getProfileUseCase() } returns flowOf(Resource.Success(profile))
        coEvery {
            updateProfileUseCase(
                nik = any(),
                phoneNumber = any(),
                address = any(),
                bankName = any(),
                accountNumber = any(),
                accountHolderName = any(),
                birthdate = any(),
                job = any(),
                companyName = any(),
                ktpFile = any(),
                kkFile = any(),
                npwpFile = any(),
                selfieFile = any(),
                salarySlipFile = any()
            )
        } returns Resource.Error(errorMessage)

        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.onEvent(
            ProfileEvent.UpdateProfile(
                nik = "1234567890123456",
                phoneNumber = "08123456789",
                address = "New Address",
                bankName = "Bank New",
                accountNumber = "9876543210",
                accountHolderName = "Test User",
                birthdate = "1990-01-01",
                job = "Developer",
                companyName = "New Company"
            )
        )
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isUpdating)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `ResetMessage event clears error and updateSuccess`() = runTest {
        // Given
        val profile = createMockProfile()
        every { getProfileUseCase() } returns flowOf(Resource.Error("Some error"))

        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.onEvent(ProfileEvent.ResetMessage)

        // Then
        val state = viewModel.uiState.value
        assertNull(state.error)
        assertFalse(state.updateSuccess)
    }

    @Test
    fun `accessToken is loaded from tokenManager`() = runTest {
        // Given
        val testToken = "my_access_token"
        every { tokenManager.getAccessToken() } returns flowOf(testToken)
        every { getProfileUseCase() } returns flowOf(Resource.Loading())

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        assertEquals(testToken, viewModel.uiState.value.accessToken)
    }

    @Test
    fun `eventFlow emits message on profile update success`() = runTest {
        // Given
        val profile = createMockProfile()
        every { getProfileUseCase() } returns flowOf(Resource.Success(profile))
        coEvery {
            updateProfileUseCase(
                nik = any(),
                phoneNumber = any(),
                address = any(),
                bankName = any(),
                accountNumber = any(),
                accountHolderName = any(),
                birthdate = any(),
                job = any(),
                companyName = any(),
                ktpFile = any(),
                kkFile = any(),
                npwpFile = any(),
                selfieFile = any(),
                salarySlipFile = any()
            )
        } returns Resource.Success(profile)

        viewModel = createViewModel()
        advanceUntilIdle()

        // When & Then
        viewModel.eventFlow.test {
            viewModel.onEvent(
                ProfileEvent.UpdateProfile(
                    nik = "1234567890123456",
                    phoneNumber = "08123456789",
                    address = "New Address",
                    bankName = "Bank New",
                    accountNumber = "9876543210",
                    accountHolderName = "Test User",
                    birthdate = "1990-01-01",
                    job = "Developer",
                    companyName = "New Company"
                )
            )

            val emission = awaitItem()
            assertEquals("Profil berhasil diperbarui", emission)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `profile isComplete returns correct value`() = runTest {
        // Given
        val incompleteProfile = createMockProfile(isComplete = false)
        every { getProfileUseCase() } returns flowOf(Resource.Success(incompleteProfile))

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.profile?.isComplete ?: true)
    }
}