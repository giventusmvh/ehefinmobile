package com.example.ehefin_mobile.feature.home.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ehefin_mobile.R
import com.example.ehefin_mobile.core.common.toCurrencyFormat
import com.example.ehefin_mobile.feature.auth.presentation.viewmodel.AuthEvent
import androidx.compose.material.icons.filled.Lock
import com.example.ehefin_mobile.feature.auth.presentation.viewmodel.AuthViewModel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.HorizontalDivider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    isLoggedIn: Boolean,
    onNavigateToPlafond: () -> Unit,
    onNavigateToSubmitLoan: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    plafondViewModel: com.example.ehefin_mobile.feature.plafond.presentation.PlafondViewModel = hiltViewModel()
) {
    val plafondState by plafondViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // Listen for logout success event (only if logged in)
    LaunchedEffect(Unit) {
        if (isLoggedIn) {
             plafondViewModel.loadPlafond()
             authViewModel.events.collect { event ->
                when (event) {
                    is AuthEvent.LogoutSuccess -> onLogout()
                    else -> {}
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "EheFin",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        if (isLoggedIn) authViewModel.logout() else onLogout() 
                    }) {
                        Icon(
                            imageVector = if (isLoggedIn) Icons.AutoMirrored.Filled.ExitToApp else Icons.Default.Lock,
                            contentDescription = if (isLoggedIn) "Keluar" else "Masuk",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Greeting & Header
            Column {
                Text(
                    text = if (isLoggedIn) "Halo, Selamat Datang!" else "Selamat Datang di EheFin",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (isLoggedIn) "Kelola keuanganmu dengan bijak hari ini." else "Solusi finansial terbaik untuk kebutuhanmu.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 2. Hero Section (Credit Card Style)
            if (!isLoggedIn) {
                GuestHeroCard(onLoginClick = onLogout)
            } else {
                val activePlafond = plafondState.activePlafond
                if (activePlafond != null) {
                    ActiveLimitCard(
                        balance = activePlafond.remainingAmount,
                        limit = activePlafond.originalAmount,
                        onDetailClick = onNavigateToPlafond
                    )
                } else {
                    ActivateLimitCard(onActivateClick = onNavigateToPlafond)
                }
            }

            // 3. Quick Actions Grid
            Column {
                Text(
                    text = "Akses Cepat",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    QuickActionItem(
                        icon = Icons.Default.Add,
                        label = "Pinjaman",
                        onClick = onNavigateToSubmitLoan,
                        enabled = isLoggedIn
                    )
                    QuickActionItem(
                        icon = Icons.Default.CreditCard,
                        label = "Plafond",
                        onClick = onNavigateToPlafond,
                        enabled = isLoggedIn
                    )
                    QuickActionItem(
                        icon = Icons.Default.History,
                        label = "Riwayat",
                        onClick = onNavigateToHistory,
                        enabled = isLoggedIn
                    )
                    QuickActionItem(
                        icon = Icons.Default.Person,
                        label = "Profil",
                        onClick = onNavigateToProfile,
                        enabled = isLoggedIn
                    )
                }
            }

            // 4. Promo / Tips Banner
            SpecialOfferBanner()
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun GuestHeroCard(onLoginClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Masuk untuk Akses Penuh",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            Text(
                text = "Nikmati kemudahan akses finansial dalam genggamanmu.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            androidx.compose.material3.Button(
                onClick = onLoginClick,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Masuk / Daftar Sekarang", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ActiveLimitCard(balance: Double, limit: Double, onDetailClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        androidx.compose.foundation.layout.Box {
            // Background decoration (circles)
            // You could allow for canvas drawing here for more complex patterns
            
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                   modifier = Modifier.fillMaxWidth(),
                   horizontalArrangement = Arrangement.SpaceBetween,
                   verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sisa Limit Tersedia",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = balance.toCurrencyFormat(),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total Limit",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        )
                        Text(
                            text = limit.toCurrencyFormat(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                             fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                        )
                    }
                    
                    androidx.compose.material3.Button(
                        onClick = onDetailClick,
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Detail", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Composable
fun ActivateLimitCard(onActivateClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
             containerColor = MaterialTheme.colorScheme.primaryContainer 
        )
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Aktifkan Limitmu!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                     color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Dapatkan limit kredit hingga Rp 50 Juta dengan bunga ringan.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                androidx.compose.material3.Button(
                    onClick = onActivateClick,
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Ajukan Sekarang")
                }
            }
        }
    }
}

@Composable
fun QuickActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(enabled = enabled, onClick = onClick)
            .width(72.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (enabled) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
             modifier = Modifier.size(56.dp)
        ) {
            androidx.compose.foundation.layout.Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                 Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
             maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}

@Composable
fun SpecialOfferBanner() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Card(
                     colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                     shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Promo Spesial",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ajak Teman dapat Cash Rp 500rb!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                 Text(
                    text = "Bagikan kode referralmu sekarang.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
            Icon(
                imageVector = Icons.Default.CardGiftcard,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}