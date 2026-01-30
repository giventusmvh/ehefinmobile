package com.example.ehefin_mobile.feature.loan.presentation.screen

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ehefin_mobile.feature.loan.presentation.viewmodel.LoanEvent
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import com.example.ehefin_mobile.feature.loan.presentation.viewmodel.LoanViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitLoanScreen(
        onLoanSubmitted: () -> Unit,
        onNavigateBack: () -> Unit,
        viewModel: LoanViewModel = hiltViewModel()
) {
    val state by viewModel.submitState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var branchExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.resetSubmitForm() }



    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        
        if (fineLocationGranted || coarseLocationGranted) {
            viewModel.fetchLocation()
        } else {
             // Handle permission denied if needed, or just proceed without location
             // For now we do nothing, user can still submit but without location
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
        viewModel.events.collectLatest { event ->
            when (event) {
                is LoanEvent.LoanSubmitted -> {
                    snackbarHostState.showSnackbar("Pinjaman berhasil diajukan!")
                    onLoanSubmitted()
                }
                is LoanEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                else -> {}
            }
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearSubmitError()
        }
    }

    Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                        title = { Text("Ajukan Pinjaman") },
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
        Column(
                modifier =
                        Modifier.fillMaxSize()
                                .padding(paddingValues)
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
        ) {
            Text(
                    text = "Formulir Pengajuan",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                    text = "Lengkapi data berikut untuk mengajukan pinjaman",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Branch Dropdown
            ExposedDropdownMenuBox(
                    expanded = branchExpanded,
                    onExpandedChange = { branchExpanded = it }
            ) {
                OutlinedTextField(
                        value = state.branches.find { it.id == state.selectedBranchId }?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Pilih Cabang") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = branchExpanded)
                        },
                        modifier =
                                Modifier.fillMaxWidth()
                                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                        expanded = branchExpanded,
                        onDismissRequest = { branchExpanded = false }
                ) {
                    state.branches.forEach { branch ->
                        DropdownMenuItem(
                                text = { Text(branch.name) },
                                onClick = {
                                    viewModel.onBranchSelected(branch.id)
                                    branchExpanded = false
                                }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Amount
            androidx.compose.material3.OutlinedTextField(
                    value = state.amount,
                    onValueChange = viewModel::onAmountChange,
                    label = { Text("Jumlah Pinjaman (Rp)") },
                    placeholder = { Text("Contoh: 10000000") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions =
                            androidx.compose.foundation.text.KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                            )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tenor
                Box(modifier = Modifier.weight(1f)) {
                    androidx.compose.material3.OutlinedTextField(
                            value = state.tenor,
                            onValueChange = viewModel::onTenorChange,
                            label = { Text("Tenor (bulan)") },
                            placeholder = { Text("1-48") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions =
                                    androidx.compose.foundation.text.KeyboardOptions(
                                            keyboardType = KeyboardType.Number
                                    )
                    )
                }

                // Interest Rate
                Box(modifier = Modifier.weight(1f)) {
                    androidx.compose.material3.OutlinedTextField(
                            value = state.interestRate,
                            onValueChange = viewModel::onInterestRateChange,
                            label = { Text("Bunga (%)") },
                            placeholder = { Text("Contoh: 5.5") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions =
                                    androidx.compose.foundation.text.KeyboardOptions(
                                            keyboardType = KeyboardType.Decimal
                                    )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Submit Button
            Button(
                    onClick = viewModel::submitLoan,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !state.isLoading &&
                            state.selectedBranchId != null &&
                            state.amount.isNotBlank() &&
                            state.tenor.isNotBlank() &&
                            state.interestRate.isNotBlank()
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = androidx.compose.ui.graphics.Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Ajukan Pinjaman")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info text
            Text(
                    text = "* Pengajuan pinjaman akan diproses setelah diverifikasi oleh tim kami.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}