package com.example.ehefin_mobile.feature.loan.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ehefin_mobile.core.common.formatToRupiah
import com.example.ehefin_mobile.core.common.toDisplayDate
import com.example.ehefin_mobile.core.designsystem.components.EheFinLoadingIndicator
import com.example.ehefin_mobile.feature.loan.presentation.components.LoanStatusBadge
import com.example.ehefin_mobile.feature.loan.presentation.viewmodel.LoanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanDetailScreen(
    loanId: Long,
    onNavigateToHistory: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: LoanViewModel = hiltViewModel()
) {
    val state by viewModel.detailState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(loanId) {
        viewModel.loadLoanDetail(loanId)
    }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Detail Pinjaman") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.isLoading && state.loan == null) {
            EheFinLoadingIndicator(
                message = "Memuat detail pinjaman...",
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            state.loan?.let { loan ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Status Badge
                    LoanStatusBadge(status = loan.status)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Amount
                    Text(
                        text = loan.requestedAmount.formatToRupiah(),
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Diajukan pada ${loan.createdAt.toDisplayDate()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Details Section
                    DetailSection(title = "Informasi Pinjaman") {
                        DetailItem("Produk", loan.productName)
                        DetailItem("Cabang", loan.branchName)
                        DetailItem("Tenor", "${loan.requestedTenor} bulan")
                        DetailItem("Suku Bunga", "${loan.requestedRate}%")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    DetailSection(title = "Data Pemohon") {
                        DetailItem("Nama", loan.customerName)
                        DetailItem("Email", loan.customerEmail)
                        loan.customerNik?.let { DetailItem("NIK", it) }
                        loan.customerPhone?.let { DetailItem("Telepon", it) }
                        loan.customerAddress?.let { DetailItem("Alamat", it) }
                    }

                    if (loan.customerBankName != null) {
                        Spacer(modifier = Modifier.height(16.dp))

                        DetailSection(title = "Informasi Bank") {
                            DetailItem("Bank", loan.customerBankName)
                            loan.customerAccountNumber?.let { DetailItem("No. Rekening", it) }
                            loan.customerAccountHolderName?.let { DetailItem("Atas Nama", it) }
                        }
                    }

                    // Approval History Section
                    if (state.history.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Riwayat Persetujuan",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        state.history.forEach { history ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                LoanStatusBadge(status = history.status)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = history.createdAt.toDisplayDate(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                history.approvedBy?.let { approver ->
                                    Text(
                                        text = "Oleh: $approver",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                history.note?.let { note ->
                                    Text(
                                        text = "Catatan: $note",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    content: @Composable () -> Unit
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(modifier = Modifier.height(8.dp))
    content()
}

@Composable
private fun DetailItem(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

