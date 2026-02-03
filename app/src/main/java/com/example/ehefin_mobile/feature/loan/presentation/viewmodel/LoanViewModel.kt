package com.example.ehefin_mobile.feature.loan.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.core.datastore.TokenManager
import com.example.ehefin_mobile.feature.loan.domain.model.Branch
import com.example.ehefin_mobile.feature.loan.domain.model.LoanApplication
import com.example.ehefin_mobile.feature.loan.domain.model.LoanHistory
import com.example.ehefin_mobile.feature.loan.domain.model.LoanItem
import com.example.ehefin_mobile.feature.loan.domain.usecase.GetBranchesUseCase
import com.example.ehefin_mobile.feature.loan.domain.usecase.GetLoanDetailUseCase
import com.example.ehefin_mobile.feature.loan.domain.usecase.GetLoanHistoryUseCase
import com.example.ehefin_mobile.feature.loan.domain.usecase.GetLoansUseCase
import com.example.ehefin_mobile.feature.loan.domain.usecase.SubmitLoanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.ehefin_mobile.feature.plafond.domain.usecase.GetPlafondUseCase
import javax.inject.Inject

/**
 * UI State for Loan List Screen
 */
data class LoanListUiState(
    val isLoading: Boolean = false,
    val loans: List<LoanItem> = emptyList(),
    val error: String? = null,
    val isOffline: Boolean = false
)

/**
 * UI State for Loan Detail Screen
 */
data class LoanDetailUiState(
    val isLoading: Boolean = false,
    val loan: LoanApplication? = null,
    val history: List<LoanHistory> = emptyList(),
    val error: String? = null
)

/**
 * UI State for Submit Loan Screen
 */
data class SubmitLoanUiState(
    val isLoading: Boolean = false,
    val branches: List<Branch> = emptyList(),
    val selectedBranchId: Long? = null,
    val amount: String = "",
    val tenor: String = "",

    val interestRate: String = "",
    val latitude: String? = null,
    val longitude: String? = null,
    val error: String? = null,
    val hasActivePlafond: Boolean? = null
)

/**
 * One-time events for navigation
 */
sealed class LoanEvent {
    object LoanSubmitted : LoanEvent()
    data class ShowError(val message: String) : LoanEvent()
    data class NavigateToDetail(val loanId: Long) : LoanEvent()
}


@HiltViewModel
class LoanViewModel @Inject constructor(
    private val getLoansUseCase: GetLoansUseCase,
    private val getLoanDetailUseCase: GetLoanDetailUseCase,
    private val getLoanHistoryUseCase: GetLoanHistoryUseCase,
    private val submitLoanUseCase: SubmitLoanUseCase,
    private val getBranchesUseCase: GetBranchesUseCase,
    private val getPlafondUseCase: GetPlafondUseCase,

    private val tokenManager: TokenManager,
    private val locationHelper: com.example.ehefin_mobile.core.util.LocationHelper
) : ViewModel() {

    
    // Loan List State
    private val _listState = MutableStateFlow(LoanListUiState())
    val listState: StateFlow<LoanListUiState> = _listState.asStateFlow()
    
    // Loan Detail State
    private val _detailState = MutableStateFlow(LoanDetailUiState())
    val detailState: StateFlow<LoanDetailUiState> = _detailState.asStateFlow()
    
    // Submit Loan State
    private val _submitState = MutableStateFlow(SubmitLoanUiState())
    val submitState: StateFlow<SubmitLoanUiState> = _submitState.asStateFlow()
    
    // Events
    private val _events = MutableSharedFlow<LoanEvent>()
    val events = _events.asSharedFlow()
    
    init {
        // Don't auto-load loans in init, let the screen trigger it
        // This ensures proper auth state check when screen is visible
    }
    
    /**
     * Load all loans for current customer
     */
    fun loadLoans() {
        viewModelScope.launch {
            // Check if user is logged in
            val isLoggedIn = tokenManager.isLoggedIn().first()
            if (!isLoggedIn) {
                _listState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Silakan login terlebih dahulu",
                        loans = emptyList()
                    )
                }
                return@launch
            }
            getLoansUseCase().collect { result ->
                _listState.update { state ->
                    when (result) {
                        is Resource.Loading -> state.copy(isLoading = true, error = null)
                        is Resource.Success -> state.copy(
                            isLoading = false,
                            loans = result.data ?: emptyList(),
                            error = null
                        )
                        is Resource.Error -> state.copy(
                            isLoading = false,
                            loans = result.data ?: state.loans, // Keep cached data
                            error = result.message,
                            isOffline = result.data != null // Has cached data but got error
                        )
                    }
                }
            }
        }
    }
    
    /**
     * Load loan detail by ID
     */
    fun loadLoanDetail(loanId: Long) {
        viewModelScope.launch {
            // Check if user is logged in
            val isLoggedIn = tokenManager.isLoggedIn().first()
            if (!isLoggedIn) {
                _detailState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Silakan login terlebih dahulu",
                        loan = null,
                        history = emptyList()
                    )
                }
                return@launch
            }
            
            _detailState.update { LoanDetailUiState(isLoading = true) }
            // Load loan detail
            launch {
                getLoanDetailUseCase(loanId).collect { result ->
                    _detailState.update { state ->
                        when (result) {
                            is Resource.Loading -> state.copy(isLoading = true)
                            is Resource.Success -> state.copy(
                                isLoading = false,
                                loan = result.data,
                                error = null
                            )
                            is Resource.Error -> state.copy(
                                isLoading = false,
                                loan = result.data,
                                error = result.message
                            )
                        }
                    }
                }
            }
            
            // Load loan history
            launch {
                getLoanHistoryUseCase(loanId).collect { result ->
                    _detailState.update { state ->
                        when (result) {
                            is Resource.Success -> state.copy(
                                history = result.data ?: emptyList()
                            )
                            is Resource.Error -> state.copy(
                                history = result.data ?: state.history
                            )
                            is Resource.Loading -> state
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Load branches for submit loan form
     */
    fun loadBranches() {
        viewModelScope.launch {
            getBranchesUseCase().collect { result ->
                _submitState.update { state ->
                    when (result) {
                        is Resource.Loading -> state.copy(isLoading = true)
                        is Resource.Success -> state.copy(
                            isLoading = false,
                            branches = result.data ?: emptyList()
                        )
                        is Resource.Error -> state.copy(
                            isLoading = false,
                            branches = result.data ?: state.branches,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    private fun loadActivePlafond() {
        viewModelScope.launch {
            _submitState.update { it.copy(isLoading = true) }
            getPlafondUseCase().collect { result ->
                _submitState.update { state ->
                    when (result) {
                        is Resource.Success -> {
                            val plafond = result.data
                            val isActive = plafond?.isActive == true
                            state.copy(
                                isLoading = false,
                                hasActivePlafond = isActive,
                                interestRate = if (isActive) plafond!!.interestRate.toString() else ""
                            )
                        }
                        is Resource.Error -> {
                            state.copy(
                                isLoading = false,
                                hasActivePlafond = false, // Assume no active plafond on error for safety, or handle differently
                                error = result.message
                            )
                        }
                        is Resource.Loading -> state // Keep loading state
                    }
                }
            }
        }
    }
    
    // Submit loan form updates
    fun onBranchSelected(branchId: Long) {
        _submitState.update { it.copy(selectedBranchId = branchId, error = null) }
    }
    
    fun onAmountChange(amount: String) {
        _submitState.update { it.copy(amount = amount, error = null) }
    }
    
    fun onTenorChange(tenor: String) {
        _submitState.update { it.copy(tenor = tenor, error = null) }
    }
    
    fun onInterestRateChange(rate: String) {
        _submitState.update { it.copy(interestRate = rate, error = null) }
    }
    
    fun clearSubmitError() {
        _submitState.update { it.copy(error = null) }
    }
    
    /**
     * Submit new loan application
     */
    fun submitLoan() {
        val state = _submitState.value
        
        // Validate
        if (state.selectedBranchId == null) {
            _submitState.update { it.copy(error = "Pilih cabang terlebih dahulu") }
            return
        }
        
        val amount = state.amount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _submitState.update { it.copy(error = "Jumlah pinjaman tidak valid") }
            return
        }
        
        val tenor = state.tenor.toIntOrNull()
        if (tenor == null || tenor <= 0) {
            _submitState.update { it.copy(error = "Tenor tidak valid") }
            return
        }
        
        val rate = state.interestRate.toDoubleOrNull()
        if (rate == null || rate <= 0) {
            _submitState.update { it.copy(error = "Suku bunga tidak valid") }
            return
        }
        
        viewModelScope.launch {
            _submitState.update { it.copy(isLoading = true, error = null) }
            
            // Fetch location immediately before submit
            val location = locationHelper.getCurrentLocation()
            
            // Check if we got location
            val finalLat = location?.latitude?.toString() ?: state.latitude
            val finalLong = location?.longitude?.toString() ?: state.longitude
            
            if (finalLat == null || finalLong == null) {
                _submitState.update { it.copy(isLoading = false, error = "Gagal mengambil lokasi. Pastikan GPS/Lokasi aktif dan izin lokasi sudah diberikan.") }
                return@launch
            }

            when (val result = submitLoanUseCase(
                branchId = state.selectedBranchId,
                amount = amount,
                tenor = tenor,
                interestRate = rate,
                latitude = finalLat,
                longitude = finalLong
            )) {
                is Resource.Success -> {
                    _submitState.update { SubmitLoanUiState() } // Reset form
                    _events.emit(LoanEvent.LoanSubmitted)
                    loadLoans() // Refresh list
                }
                is Resource.Error -> {
                    _submitState.update { 
                        it.copy(isLoading = false, error = result.message) 
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }
    
    fun fetchLocation() {
        viewModelScope.launch {
            val location = locationHelper.getCurrentLocation()
            if (location != null) {
                _submitState.update { 
                    it.copy(
                        latitude = location.latitude.toString(),
                        longitude = location.longitude.toString()
                    ) 
                }
            }
        }
    }

    fun resetSubmitForm() {
        _submitState.update { SubmitLoanUiState() }
        loadBranches()
        loadActivePlafond()
    }
}