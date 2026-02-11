package com.example.ehefin_mobile.feature.plafond.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import com.example.ehefin_mobile.navigation.Screen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ehefin_mobile.R
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ehefin_mobile.core.common.toCurrencyFormat
import com.example.ehefin_mobile.feature.plafond.domain.model.Product
import com.example.ehefin_mobile.feature.plafond.domain.model.UserPlafond
import kotlinx.coroutines.flow.collectLatest

import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlafondScreen(navController: NavController, viewModel: PlafondViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is PlafondEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is PlafondEvent.PlafondSelected -> {
                    // Refresh UI or navigate back
                }
            }
        }
    }

    if (uiState.showIncompleteProfileDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissIncompleteProfileDialog() },
            title = { Text(stringResource(R.string.plafond_incomplete_title)) },
            text = {
                Text(stringResource(R.string.plafond_incomplete_desc))
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.dismissIncompleteProfileDialog()
                        navController.navigate(Screen.EditProfile.route)
                    }
                ) {
                    Text(stringResource(R.string.plafond_complete_profile))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissIncompleteProfileDialog() }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                        title = { Text(stringResource(R.string.plafond_my), fontWeight = FontWeight.Bold) },
                        navigationIcon = {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(R.string.back)
                                )
                            }
                        }
                )
            }
    ) { paddingValues ->
        if (uiState.isLoading && uiState.activePlafond == null && uiState.products.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Active Plafond Section
                item {
                    Text(
                            text = stringResource(R.string.plafond_status_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                item {
                    if (uiState.activePlafond != null) {
                         // Recreating the card style from Home for consistency, or reusing if shared. 
                         // For now locally defining similar style for simplicity in this file.
                        ActivePlafondCard(plafond = uiState.activePlafond!!)
                    } else {
                        NoPlafondCard()
                    }
                }

                // Products Section
                if (uiState.activePlafond == null && uiState.products.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Column {
                             Text(
                                text = stringResource(R.string.plafond_product_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = stringResource(R.string.plafond_product_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    items(uiState.products) { product ->
                        ProductCard(
                                product = product,
                                isSelecting = uiState.isSelectingPlafond,
                                onSelect = { viewModel.selectPlafond(product.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, isSelecting: Boolean, onSelect: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isSelecting, onClick = onSelect),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Product Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.plafond_limit_up_to),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = product.amount.toCurrencyFormat(),
                        style = MaterialTheme.typography.headlineSmall, // Make it big
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Right: Action (Arrow or Button)
                IconButton(
                    onClick = onSelect,
                    enabled = !isSelecting,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    if (isSelecting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = stringResource(R.string.plafond_select)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Bottom Info Row (Tenor & Interest)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.plafond_tenor_format, product.tenor),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Percent, // Or similar
                        contentDescription = null, // Or just text "%"
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                     Text(
                        text = stringResource(R.string.plafond_rate_per_month, product.interestRate.toString()),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ActivePlafondCard(plafond: UserPlafond) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors =
                    CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary
                    ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                        imageVector = Icons.Filled.CreditCard,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                        text = stringResource(R.string.plafond_priority_card),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = stringResource(R.string.plafond_remaining_limit),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
            
            Text(
                text = plafond.remainingAmount.toCurrencyFormat(),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            // Progress Bar
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                     Text(
                        text = stringResource(R.string.plafond_used),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "${((1f - (plafond.remainingAmount.toFloat() / plafond.originalAmount.toFloat())) * 100).toInt()}%",
                         style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                        progress = {
                            1f - (plafond.remainingAmount / plafond.originalAmount).toFloat()
                        },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.surface, // White progress
                        trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f),
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                 InfoValueItem(stringResource(R.string.plafond_total_limit), plafond.originalAmount.toCurrencyFormat(), true)
                 InfoValueItem(stringResource(R.string.loan_card_tenor), stringResource(R.string.plafond_tenor_format, plafond.tenor), true)
            }
        }
    }
}

@Composable
fun InfoValueItem(label: String, value: String, isLight: Boolean) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isLight) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (isLight) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun NoPlafondCard() {
    Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors =
                    CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
    ) {
        Column(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                horizontalAlignment = Alignment.Start
        ) {
            Icon(
                    imageVector = Icons.Filled.CreditCard,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                    text = stringResource(R.string.plafond_not_active),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                    text = stringResource(R.string.plafond_not_active_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}