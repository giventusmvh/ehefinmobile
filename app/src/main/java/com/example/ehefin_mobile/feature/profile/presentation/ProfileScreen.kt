package com.example.ehefin_mobile.feature.profile.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ehefin_mobile.navigation.Screen
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(key1 = true) {
        viewModel.onEvent(ProfileEvent.Refresh)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Saya") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.EditProfile.route) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profil")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (uiState.isLoading && uiState.profile == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                RefreshIndicator(
                    isRefreshing = uiState.isLoading,
                    onRefresh = { viewModel.onEvent(ProfileEvent.Refresh) }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Profile Header
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = uiState.profile?.name ?: "-",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = uiState.profile?.email ?: "-",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )
                            }
                        }

                        // Personal Info
                        SectionHeader(title = "Data Diri")
                        InfoItem(label = "NIK", value = uiState.profile?.nik)
                        InfoItem(label = "Tanggal Lahir", value = uiState.profile?.birthdate)
                        InfoItem(label = "No. Telepon", value = uiState.profile?.phoneNumber)
                        InfoItem(label = "Alamat", value = uiState.profile?.address)

                        // Bank Info
                        SectionHeader(title = "Data Rekening")
                        InfoItem(label = "Nama Bank", value = uiState.profile?.bankName)
                        InfoItem(label = "No. Rekening", value = uiState.profile?.accountNumber)
                        InfoItem(label = "Nama Pemilik", value = uiState.profile?.accountHolderName)

                        // Documents
                        SectionHeader(title = "Dokumen")
                        DocumentStatusItem(
                            label = "KTP",
                            imageUrl = uiState.profile?.ktpPath,
                            accessToken = uiState.accessToken
                        )
                        DocumentStatusItem(
                            label = "Kartu Keluarga",
                            imageUrl = uiState.profile?.kkPath,
                            accessToken = uiState.accessToken
                        )
                        DocumentStatusItem(
                            label = "NPWP",
                            imageUrl = uiState.profile?.npwpPath,
                            accessToken = uiState.accessToken
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (uiState.profile?.isComplete == false) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                            ) {
                                Text(
                                    text = "Profil belum lengkap. Mohon lengkapi data diri dan dokumen Anda.",
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                            Button(
                                onClick = { navController.navigate(Screen.EditProfile.route) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Lengkapi Profil")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
    Divider()
}

@Composable
fun InfoItem(label: String, value: String?) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = if (value.isNullOrBlank()) "-" else value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun DocumentStatusItem(label: String, imageUrl: String?, accessToken: String?) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
            val isUploaded = !imageUrl.isNullOrEmpty()
            if (isUploaded) {
                Text(
                    text = "Terupload",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )
            } else {
                Text(
                    text = "Belum Ada",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
        
        if (!imageUrl.isNullOrEmpty()) {
            val context = androidx.compose.ui.platform.LocalContext.current
            
            // Build request with auth header
            val model = remember(imageUrl, accessToken) {
                if (accessToken != null) {
                    coil.request.ImageRequest.Builder(context)
                        .data(imageUrl)
                        .addHeader("Authorization", "Bearer $accessToken")
                        .crossfade(true)
                        .build()
                } else {
                    coil.request.ImageRequest.Builder(context)
                        .data(imageUrl)
                        .crossfade(true)
                        .build()
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            AsyncImage(
                model = model,
                contentDescription = "Preview $label",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(4.dp),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
        }
    }
}


@Composable
fun RefreshIndicator(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    // In a real app we'd use PullRefresh or similar. For now just passing content.
    content()
}
