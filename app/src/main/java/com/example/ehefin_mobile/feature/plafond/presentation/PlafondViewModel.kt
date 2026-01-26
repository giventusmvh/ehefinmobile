package com.example.ehefin_mobile.feature.plafond.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.feature.plafond.domain.model.Product
import com.example.ehefin_mobile.feature.plafond.domain.model.UserPlafond
import com.example.ehefin_mobile.feature.plafond.domain.usecase.GetPlafondUseCase
import com.example.ehefin_mobile.feature.plafond.domain.usecase.GetProductsUseCase
import com.example.ehefin_mobile.feature.plafond.domain.usecase.SelectPlafondUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlafondUiState(
        val isLoading: Boolean = false,
        val activePlafond: UserPlafond? = null,
        val products: List<Product> = emptyList(),
        val error: String? = null,
        val isSelectingPlafond: Boolean = false
)

sealed class PlafondEvent {
    data class ShowMessage(val message: String) : PlafondEvent()
    object PlafondSelected : PlafondEvent()
}

@HiltViewModel
class PlafondViewModel
@Inject
constructor(
        private val getPlafondUseCase: GetPlafondUseCase,
        private val getProductsUseCase: GetProductsUseCase,
        private val selectPlafondUseCase: SelectPlafondUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlafondUiState())
    val uiState: StateFlow<PlafondUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PlafondEvent>()
    val events = _events.asSharedFlow()

    init {
        loadPlafond()
        loadProducts()
    }

    fun loadPlafond() {
        viewModelScope.launch {
            getPlafondUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(isLoading = false, activePlafond = result.data, error = null)
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                    isLoading = false,
                                    activePlafond = result.data,
                                    error = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun loadProducts() {
        viewModelScope.launch {
            getProductsUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        // Already showing loading from plafond
                    }
                    is Resource.Success -> {
                        _uiState.update { it.copy(products = result.data ?: emptyList()) }
                    }
                    is Resource.Error -> {
                        // Products failure is secondary, don't override main error
                        if (_uiState.value.error == null) {
                            _uiState.update { it.copy(error = result.message) }
                        }
                    }
                }
            }
        }
    }

    fun selectPlafond(productId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSelectingPlafond = true, error = null) }

            when (val result = selectPlafondUseCase(productId)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(isSelectingPlafond = false, activePlafond = result.data)
                    }
                    _events.emit(PlafondEvent.ShowMessage("Plafond berhasil dipilih!"))
                    _events.emit(PlafondEvent.PlafondSelected)
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isSelectingPlafond = false, error = result.message) }
                    _events.emit(
                            PlafondEvent.ShowMessage(result.message ?: "Gagal memilih plafond")
                    )
                }
                is Resource.Loading -> {
                    /* Handled by isSelectingPlafond */
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
