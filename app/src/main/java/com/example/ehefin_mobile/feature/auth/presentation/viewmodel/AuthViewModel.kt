package com.example.ehefin_mobile.feature.auth.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.feature.auth.domain.usecase.FirebaseLoginUseCase
import com.example.ehefin_mobile.feature.auth.domain.usecase.ForgotPasswordUseCase
import com.example.ehefin_mobile.feature.auth.domain.usecase.LoginUseCase
import com.example.ehefin_mobile.feature.auth.domain.usecase.LogoutUseCase
import com.example.ehefin_mobile.feature.auth.domain.usecase.RegisterUseCase
import com.example.ehefin_mobile.feature.profile.domain.usecase.RegisterFcmTokenUseCase
import com.example.ehefin_mobile.feature.profile.domain.repository.ProfileRepository
import com.example.ehefin_mobile.feature.loan.domain.repository.LoanRepository
import com.example.ehefin_mobile.feature.plafond.domain.repository.PlafondRepository
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * UI State for authentication screens
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    
    // Login form
    val loginEmail: String = "",
    val loginPassword: String = "",
    
    // Register form
    val registerName: String = "",
    val registerEmail: String = "",
    val registerPassword: String = "",
    val registerConfirmPassword: String = "",
    
    // Forgot password
    val forgotPasswordEmail: String = ""
)

/**
 * One-time events for navigation
 */
sealed class AuthEvent {
    object LoginSuccess : AuthEvent()
    object RegisterSuccess : AuthEvent()
    object LogoutSuccess : AuthEvent()
    object ForgotPasswordEmailSent : AuthEvent()
    data class ShowError(val message: String) : AuthEvent()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val firebaseLoginUseCase: FirebaseLoginUseCase,
    private val registerFcmTokenUseCase: RegisterFcmTokenUseCase,
    private val profileRepository: ProfileRepository,
    private val loanRepository: LoanRepository,
    private val plafondRepository: PlafondRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    private val _events = MutableSharedFlow<AuthEvent>()
    val events = _events.asSharedFlow()
    
    // Login form updates
    fun onLoginEmailChange(email: String) {
        _uiState.update { it.copy(loginEmail = email, error = null) }
    }
    
    fun onLoginPasswordChange(password: String) {
        _uiState.update { it.copy(loginPassword = password, error = null) }
    }
    
    // Register form updates
    fun onRegisterNameChange(name: String) {
        _uiState.update { it.copy(registerName = name, error = null) }
    }
    
    fun onRegisterEmailChange(email: String) {
        _uiState.update { it.copy(registerEmail = email, error = null) }
    }
    
    fun onRegisterPasswordChange(password: String) {
        _uiState.update { it.copy(registerPassword = password, error = null) }
    }
    
    fun onRegisterConfirmPasswordChange(confirmPassword: String) {
        _uiState.update { it.copy(registerConfirmPassword = confirmPassword, error = null) }
    }
    
    // Forgot password form
    fun onForgotPasswordEmailChange(email: String) {
        _uiState.update { it.copy(forgotPasswordEmail = email, error = null) }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    /**
     * Register FCM token to backend after successful authentication.
     * This enables push notifications for loan status updates.
     */
    private fun registerFcmToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            Log.d("FCM", "Registering FCM token: $token")
            viewModelScope.launch {
                when (val result = registerFcmTokenUseCase(token)) {
                    is Resource.Success -> {
                        Log.d("FCM", "FCM token registered successfully")
                    }
                    is Resource.Error -> {
                        Log.e("FCM", "Failed to register FCM token: ${result.message}")
                    }
                    is Resource.Loading -> {}
                }
            }
        }.addOnFailureListener { e ->
            Log.e("FCM", "Failed to get FCM token", e)
        }
    }

    /**
     * Sync user data (Profile, Loans, Plafond) to local DB
     */
    private suspend fun syncUserData() {
        // Use supervisorScope to ensure one failure doesn't cancel others
        supervisorScope {
            // Run in parallel
            val profileJob = async { 
                try {
                    profileRepository.refreshProfile() 
                } catch (e: Exception) {
                    Log.e("AuthViewModel", "Failed to sync profile", e)
                }
            }
            val loansJob = async { 
                try {
                    loanRepository.refreshLoans() 
                } catch (e: Exception) {
                    Log.e("AuthViewModel", "Failed to sync loans", e)
                }
            }
            val plafondJob = async { 
                try {
                    plafondRepository.refreshPlafond() 
                } catch (e: Exception) {
                    Log.e("AuthViewModel", "Failed to sync plafond", e)
                }
            }
            
            // Wait for all to complete (or fail safely)
            profileJob.await()
            loansJob.await()
            plafondJob.await()
        }
    }
    
    fun login() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = loginUseCase(
                email = _uiState.value.loginEmail,
                password = _uiState.value.loginPassword
            )) {
                is Resource.Success -> {
                    // Start syncing data while still showing loading
                    try {
                        syncUserData()
                    } catch (e: Exception) {
                        Log.e("AuthViewModel", "Sync failed during login", e)
                        // Continue anyway, don't block login
                    }
                    
                    _uiState.update { it.copy(isLoading = false) }
                    registerFcmToken() // Register FCM token after login
                    _events.emit(AuthEvent.LoginSuccess)
                }
                is Resource.Error -> {
                    _uiState.update { 
                        it.copy(isLoading = false, error = result.message) 
                    }
                }
                is Resource.Loading -> {
                    // Already handled
                }
            }
        }
    }
    
    fun register() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = registerUseCase(
                name = _uiState.value.registerName,
                email = _uiState.value.registerEmail,
                password = _uiState.value.registerPassword,
                confirmPassword = _uiState.value.registerConfirmPassword
            )) {
                is Resource.Success -> {
                    // Start syncing data while still showing loading
                    try {
                        syncUserData()
                    } catch (e: Exception) {
                        Log.e("AuthViewModel", "Sync failed during register", e)
                    }

                    _uiState.update { it.copy(isLoading = false) }
                    registerFcmToken() // Register FCM token after registration
                    _events.emit(AuthEvent.RegisterSuccess)
                }
                is Resource.Error -> {
                    _uiState.update { 
                        it.copy(isLoading = false, error = result.message) 
                    }
                }
                is Resource.Loading -> {
                    // Already handled
                }
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (logoutUseCase()) {
                is Resource.Success -> {
                    _uiState.update { AuthUiState() } // Reset state
                    _events.emit(AuthEvent.LogoutSuccess)
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(AuthEvent.LogoutSuccess) // Logout anyway
                }
                is Resource.Loading -> {}
            }
        }
    }
    
    fun forgotPassword() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = forgotPasswordUseCase(
                email = _uiState.value.forgotPasswordEmail
            )) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(AuthEvent.ForgotPasswordEmailSent)
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    /**
     * Login with Firebase ID token (Google Sign-In)
     * @param idToken Firebase ID token from Google Sign-In
     */
    fun loginWithFirebase(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Get FCM token if available
            val fcmToken = try {
                com.google.firebase.messaging.FirebaseMessaging.getInstance().token.await()
            } catch (e: Exception) {
                Log.e("FCM", "Failed to get FCM token", e)
                null
            }

            when (val result = firebaseLoginUseCase(idToken, fcmToken)) {
                is Resource.Success -> {
                    // Start syncing data while still showing loading
                    try {
                        syncUserData()
                    } catch (e: Exception) {
                        Log.e("AuthViewModel", "Sync failed during firebase login", e)
                    }

                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(AuthEvent.LoginSuccess)
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
                is Resource.Loading -> {
                    // Already handled
                }
            }
        }
    }
}