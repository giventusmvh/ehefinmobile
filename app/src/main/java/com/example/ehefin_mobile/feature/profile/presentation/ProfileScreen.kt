package com.example.ehefin_mobile.feature.profile.presentation

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ehefin_mobile.R
import com.example.ehefin_mobile.feature.profile.domain.model.UserProfile
import com.example.ehefin_mobile.navigation.Screen

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
                title = { Text(stringResource(R.string.profile_title)) },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.EditProfile.route) }) {
                        Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit_profile))
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
        Box(modifier = Modifier.padding(paddingValues)) {
            if (uiState.isLoading && uiState.profile == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                ) {
                    // Profile Header
                    uiState.profile?.let { profile ->
                        ProfileHeader(
                            profile = profile,
                            accessToken = uiState.accessToken,
                            onEditClick = { navController.navigate(Screen.EditProfile.route) }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    if (uiState.profile?.isComplete == false) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = stringResource(R.string.profile_incomplete),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = stringResource(R.string.profile_incomplete_desc),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                            Button(
                                onClick = { navController.navigate(Screen.EditProfile.route) },
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text(stringResource(R.string.complete_now))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    // Personal Info
                    ProfileSection(
                        title = stringResource(R.string.personal_info),
                        icon = Icons.Default.Person
                    ) {
                        ProfileInfoRow(label = stringResource(R.string.label_nik), value = uiState.profile?.nik)
                        ProfileInfoRow(label = stringResource(R.string.label_birthdate), value = uiState.profile?.birthdate)
                        ProfileInfoRow(label = stringResource(R.string.label_job), value = uiState.profile?.job)
                        ProfileInfoRow(label = stringResource(R.string.label_company), value = uiState.profile?.companyName)
                        ProfileInfoRow(label = stringResource(R.string.label_phone), value = uiState.profile?.phoneNumber)
                        ProfileInfoRow(label = stringResource(R.string.label_address), value = uiState.profile?.address)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bank Info
                    ProfileSection(
                        title = stringResource(R.string.bank_info),
                        icon = Icons.Default.AccountBalance
                    ) {
                        ProfileInfoRow(label = stringResource(R.string.label_bank_name), value = uiState.profile?.bankName)
                        ProfileInfoRow(label = stringResource(R.string.label_account_number), value = uiState.profile?.accountNumber)
                        ProfileInfoRow(label = stringResource(R.string.label_account_holder), value = uiState.profile?.accountHolderName)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Documents
                    ProfileSection(
                        title = stringResource(R.string.documents),
                        icon = Icons.Default.Description
                    ) {
                        DocumentItem(
                            label = stringResource(R.string.label_ktp),
                            imageUrl = uiState.profile?.ktpPath,
                            accessToken = uiState.accessToken
                        )
                        DocumentItem(
                            label = stringResource(R.string.label_kk),
                            imageUrl = uiState.profile?.kkPath,
                            accessToken = uiState.accessToken
                        )
                        DocumentItem(
                            label = stringResource(R.string.label_npwp),
                            imageUrl = uiState.profile?.npwpPath,
                            accessToken = uiState.accessToken
                        )
                        DocumentItem(
                            label = stringResource(R.string.label_selfie),
                            imageUrl = uiState.profile?.selfiePath,
                            accessToken = uiState.accessToken
                        )
                        DocumentItem(
                            label = stringResource(R.string.label_salary_slip),
                            imageUrl = uiState.profile?.salarySlipPath,
                            accessToken = uiState.accessToken
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(
    profile: UserProfile,
    accessToken: String? = null,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                if (!profile.selfiePath.isNullOrEmpty()) {
                    val context = LocalContext.current
                    // Build request with auth header
                    val model = remember(profile.selfiePath, accessToken) {
                        if (accessToken != null) {
                            ImageRequest.Builder(context)
                                .data(profile.selfiePath)
                                .addHeader("Authorization", "Bearer $accessToken")
                                .crossfade(true)
                                .build()
                        } else {
                            ImageRequest.Builder(context)
                                .data(profile.selfiePath)
                                .crossfade(true)
                                .build()
                        }
                    }
                    AsyncImage(
                        model = model,
                        contentDescription = stringResource(R.string.profile_photo),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Text(
                        text = profile.name.take(1).uppercase(),
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = profile.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = profile.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun ProfileSection(
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
fun ProfileInfoRow(label: String, value: String?) {
    val emptyValue = stringResource(R.string.empty_value)
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
            text = if (value.isNullOrBlank()) emptyValue else value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun DocumentItem(label: String, imageUrl: String?, accessToken: String?) {
    val isUploaded = !imageUrl.isNullOrEmpty()
    val uploadedText = stringResource(R.string.doc_uploaded)
    val notUploadedText = stringResource(R.string.doc_not_uploaded)
    val previewText = stringResource(R.string.preview_document, label)
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            if (isUploaded) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = uploadedText,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Text(
                    text = notUploadedText,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        
        if (isUploaded) {
            Spacer(modifier = Modifier.height(8.dp))
            val context = LocalContext.current
            
            // Build request with auth header
            val model = remember(imageUrl, accessToken) {
                if (accessToken != null) {
                    ImageRequest.Builder(context)
                        .data(imageUrl)
                        .addHeader("Authorization", "Bearer $accessToken")
                        .crossfade(true)
                        .build()
                } else {
                    ImageRequest.Builder(context)
                        .data(imageUrl)
                        .crossfade(true)
                        .build()
                }
            }

            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(160.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                 AsyncImage(
                    model = model,
                    contentDescription = previewText,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    error = androidx.compose.ui.graphics.vector.rememberVectorPainter(Icons.Default.Warning)
                )
            }

        }
    }
}