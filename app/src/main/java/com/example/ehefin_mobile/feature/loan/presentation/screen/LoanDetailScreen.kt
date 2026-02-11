package com.example.ehefin_mobile.feature.loan.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ehefin_mobile.R
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ehefin_mobile.core.common.formatToRupiah
import com.example.ehefin_mobile.core.common.toDisplayDate
import com.example.ehefin_mobile.core.designsystem.components.EheFinLoadingIndicator
import com.example.ehefin_mobile.core.designsystem.theme.StatusApproved
import com.example.ehefin_mobile.core.designsystem.theme.StatusPending
import com.example.ehefin_mobile.core.designsystem.theme.StatusRejected
import com.example.ehefin_mobile.feature.loan.domain.model.LoanApplication
import com.example.ehefin_mobile.feature.loan.domain.model.LoanHistory
import com.example.ehefin_mobile.feature.loan.domain.model.LoanStatus
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
                title = { Text(stringResource(R.string.loan_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (state.isLoading && state.loan == null) {
            EheFinLoadingIndicator(
                message = stringResource(R.string.loan_detail_loading),
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
                    // Header
                    LoanHeaderSection(loan = loan)

                    Spacer(modifier = Modifier.height(24.dp))

                    // Sections
                    InfoSection(
                        title = stringResource(R.string.loan_detail_info),
                        icon = Icons.Default.Info
                    ) {
                        InfoRow(stringResource(R.string.loan_detail_product), loan.productName)
                        InfoRow(stringResource(R.string.select_branch), loan.branchName)
                        InfoRow(stringResource(R.string.loan_card_tenor), stringResource(R.string.loan_detail_tenor_format, loan.requestedTenor))
                        InfoRow(stringResource(R.string.loan_card_interest), stringResource(R.string.loan_detail_rate_format, loan.requestedRate.toString()))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    InfoSection(
                        title = stringResource(R.string.loan_detail_applicant),
                        icon = Icons.Default.Person
                    ) {
                        InfoRow(stringResource(R.string.loan_detail_name), loan.customerName)
                        InfoRow(stringResource(R.string.label_email), loan.customerEmail)
                        loan.customerNik?.let { InfoRow(stringResource(R.string.loan_detail_nik), it) }
                        loan.customerPhone?.let { InfoRow(stringResource(R.string.loan_detail_phone), it) }
                    }

                    if (loan.customerBankName != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        InfoSection(
                            title = stringResource(R.string.loan_detail_bank_info),
                            icon = Icons.Default.AccountBalance
                        ) {
                            InfoRow(stringResource(R.string.loan_detail_bank), loan.customerBankName)
                            loan.customerAccountNumber?.let { InfoRow(stringResource(R.string.loan_detail_account_number), it) }
                            loan.customerAccountHolderName?.let { InfoRow(stringResource(R.string.loan_detail_account_holder), it) }
                        }
                    }

                    // Timeline
                    if (state.history.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = stringResource(R.string.loan_detail_status_history),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        state.history.forEachIndexed { index, history ->
                            TimelineItem(
                                history = history,
                                isLast = index == state.history.lastIndex
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun LoanHeaderSection(loan: LoanApplication) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoanStatusBadge(status = loan.status)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.loan_amount),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            Text(
                text = loan.requestedAmount.formatToRupiah(),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = loan.createdAt.toDisplayDate(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun InfoSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun TimelineItem(
    history: LoanHistory,
    isLast: Boolean
) {
    val status = history.status
    
    val (icon, color) = when {
        status.isApproved() -> Icons.Outlined.CheckCircle to StatusApproved
        status.isRejected() -> Icons.Default.Close to StatusRejected
        else -> Icons.Default.HourglassEmpty to StatusPending
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min) 
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f))
                    .border(1.dp, color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            
            // Connecting Line
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier
                .padding(bottom = if (isLast) 0.dp else 32.dp)
                .weight(1f)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = getStatusDisplayName(status),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = history.createdAt.toDisplayDate(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    if (!history.approvedBy.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        val roleText = if (!history.approvedByRole.isNullOrEmpty()) " (${history.approvedByRole})" else ""
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${history.approvedBy}$roleText",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (!history.note.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        ) {
                            Text(
                                text = history.note,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

// Helper to get consistent display name
@Composable
fun getStatusDisplayName(status: LoanStatus): String {
    return when (status) {
        LoanStatus.SUBMITTED -> stringResource(R.string.loan_status_submitted)
        LoanStatus.MARKETING_APPROVED -> stringResource(R.string.loan_status_marketing_approved)
        LoanStatus.MARKETING_REJECTED -> stringResource(R.string.loan_status_marketing_rejected)
        LoanStatus.BRANCH_MANAGER_APPROVED -> stringResource(R.string.loan_status_bm_approved)
        LoanStatus.BRANCH_MANAGER_REJECTED -> stringResource(R.string.loan_status_bm_rejected)
        LoanStatus.DISBURSED -> stringResource(R.string.loan_status_disbursed)
        LoanStatus.REJECTED -> stringResource(R.string.loan_status_rejected)
    }
}