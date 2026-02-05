package com.example.ehefin_mobile.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.feature.profile.domain.model.UserProfile
import com.example.ehefin_mobile.feature.profile.domain.usecase.GetProfileUseCase
import com.example.ehefin_mobile.feature.profile.domain.usecase.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
        val isLoading: Boolean = false,
        val profile: UserProfile? = null,
        val error: String? = null,
        val isUpdating: Boolean = false,
        val updateSuccess: Boolean = false,
        val accessToken: String? = null
)

sealed class ProfileEvent {
    object Refresh : ProfileEvent()
    data class UpdateProfile(
            val nik: String,
            val phoneNumber: String,
            val address: String,
            val bankName: String,
            val accountNumber: String,
            val accountHolderName: String,
            val birthdate: String,
            val job: String,
            val companyName: String,
            val ktpFile: File? = null,
            val kkFile: File? = null,
            val npwpFile: File? = null,
            val selfieFile: File? = null,
            val salarySlipFile: File? = null
    ) : ProfileEvent()
    object ResetMessage : ProfileEvent() // Reset success/error messages
}

@HiltViewModel
class ProfileViewModel
@Inject
constructor(
        private val getProfileUseCase: GetProfileUseCase,
        private val updateProfileUseCase: UpdateProfileUseCase,
        private val tokenManager: com.example.ehefin_mobile.core.datastore.TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        // Load token
        viewModelScope.launch {
            tokenManager.getAccessToken().collect { token ->
                _uiState.update { it.copy(accessToken = token) }
            }
        }
        loadProfile()
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.Refresh -> {
                loadProfile()
            }
            is ProfileEvent.UpdateProfile -> {
                updateProfile(event)
            }
            is ProfileEvent.ResetMessage -> {
                _uiState.update { it.copy(error = null, updateSuccess = false) }
            }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            getProfileUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(isLoading = false, profile = result.data, error = null)
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                    isLoading = false,
                                    profile = result.data, // Show cached if available
                                    error = result.message
                            )
                        }
                        if (result.message != null) {
                            _eventFlow.emit(result.message)
                        }
                    }
                }
            }
        }
    }

    private fun updateProfile(event: ProfileEvent.UpdateProfile) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, error = null) }

            val result =
                    updateProfileUseCase(
                            nik = event.nik,
                            phoneNumber = event.phoneNumber,
                            address = event.address,
                            bankName = event.bankName,
                            accountNumber = event.accountNumber,
                            accountHolderName = event.accountHolderName,
                            birthdate = event.birthdate,
                            job = event.job,
                            companyName = event.companyName,
                            ktpFile = event.ktpFile,
                            kkFile = event.kkFile,
                            npwpFile = event.npwpFile,
                            selfieFile = event.selfieFile,
                            salarySlipFile = event.salarySlipFile
                    )

            when (result) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(isUpdating = false, updateSuccess = true, profile = result.data)
                    }
                    _eventFlow.emit("Profil berhasil diperbarui")
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isUpdating = false, error = result.message) }
                    _eventFlow.emit(result.message ?: "Gagal update profil")
                }
                is Resource.Loading -> {
                    // Handled by isUpdating
                }
            }
        }
    }
}