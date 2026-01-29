package com.example.ehefin_mobile.feature.loan.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ehefin_mobile.core.designsystem.components.EheFinEmptyState
import com.example.ehefin_mobile.core.designsystem.components.EheFinErrorMessage
import com.example.ehefin_mobile.core.designsystem.components.EheFinLoadingIndicator
import com.example.ehefin_mobile.core.designsystem.components.EheFinOfflineBanner
import com.example.ehefin_mobile.feature.loan.presentation.components.LoanCard
import com.example.ehefin_mobile.feature.loan.presentation.viewmodel.LoanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanListScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToSubmit: () -> Unit,
    viewModel: LoanViewModel = hiltViewModel()
) {
    val state by viewModel.listState.collectAsState()

    // Load loans when screen is visible
    LaunchedEffect(Unit) {
        viewModel.loadLoans()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Pinjaman") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToSubmit,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Ajukan Pinjaman",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Offline Banner
            EheFinOfflineBanner(isVisible = state.isOffline)

            when {
                state.isLoading && state.loans.isEmpty() -> {
                    EheFinLoadingIndicator(message = "Memuat data pinjaman...")
                }

                state.error != null && state.loans.isEmpty() -> {
                    EheFinErrorMessage(
                        message = state.error ?: "Terjadi kesalahan",
                        onRetry = { viewModel.loadLoans() }
                    )
                }

                state.loans.isEmpty() -> {
                    EheFinEmptyState(
                        title = "Belum Ada Pinjaman",
                        message = "Anda belum memiliki pinjaman. Ajukan pinjaman pertama Anda sekarang!",
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = state.loans,
                            key = { it.id }
                        ) { loan ->
                            LoanCard(
                                loan = loan,
                                onClick = { onNavigateToDetail(loan.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}