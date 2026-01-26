package com.example.ehefin_mobile.feature.profile.presentation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    // State for form fields
    var nik by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var accountHolderName by remember { mutableStateOf("") }
    var birthdate by remember { mutableStateOf("") } // YYYY-MM-DD format for simplicity

    // State for document files
    var ktpFile by remember { mutableStateOf<File?>(null) }
    var kkFile by remember { mutableStateOf<File?>(null) }
    var npwpFile by remember { mutableStateOf<File?>(null) }

    // Initialize state from profile
    LaunchedEffect(uiState.profile) {
        uiState.profile?.let {
            if (nik.isEmpty()) nik = it.nik ?: ""
            if (phoneNumber.isEmpty()) phoneNumber = it.phoneNumber ?: ""
            if (address.isEmpty()) address = it.address ?: ""
            if (bankName.isEmpty()) bankName = it.bankName ?: ""
            if (accountNumber.isEmpty()) accountNumber = it.accountNumber ?: ""
            if (accountHolderName.isEmpty()) accountHolderName = it.accountHolderName ?: ""
            if (birthdate.isEmpty()) birthdate = it.birthdate ?: ""
        }
    }

    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            snackbarHostState.showSnackbar("Profil berhasil disimpan")
            navController.navigateUp()
        }
    }

    // Using a map to track which doc is being uploaded
    var activeUploadType by remember { mutableStateOf<String?>(null) }

    val documentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            activeUploadType?.let { type ->
                val file = uriToFile(context, selectedUri)
                if (file != null) {
                    when (type) {
                        "KTP" -> ktpFile = file
                        "KK" -> kkFile = file
                        "NPWP" -> npwpFile = file
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profil") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = nik,
                onValueChange = { if (it.length <= 16) nik = it },
                label = { Text("NIK") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = birthdate,
                onValueChange = { birthdate = it },
                label = { Text("Tanggal Lahir (YYYY-MM-DD)") },
                 keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Nomor Telepon") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Alamat Lengkap") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            SectionHeader(title = "Info Rekening")

            OutlinedTextField(
                value = bankName,
                onValueChange = { bankName = it },
                label = { Text("Nama Bank") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = accountNumber,
                onValueChange = { accountNumber = it },
                label = { Text("Nomor Rekening") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = accountHolderName,
                onValueChange = { accountHolderName = it },
                label = { Text("Nama Pemilik Rekening") },
                modifier = Modifier.fillMaxWidth()
            )

            SectionHeader(title = "Upload Dokumen")

            DocumentUploadItem(
                label = "KTP",
                isUploaded = ktpFile != null || !uiState.profile?.ktpPath.isNullOrEmpty(),
                isPending = ktpFile != null,
                onUpload = {
                    activeUploadType = "KTP"
                    documentLauncher.launch("image/*")
                }
            )

            DocumentUploadItem(
                label = "Kartu Keluarga",
                isUploaded = kkFile != null || !uiState.profile?.kkPath.isNullOrEmpty(),
                isPending = kkFile != null,
                onUpload = {
                    activeUploadType = "KK"
                    documentLauncher.launch("image/*")
                }
            )

            DocumentUploadItem(
                label = "NPWP",
                isUploaded = npwpFile != null || !uiState.profile?.npwpPath.isNullOrEmpty(),
                isPending = npwpFile != null,
                onUpload = {
                    activeUploadType = "NPWP"
                    documentLauncher.launch("image/*")
                }
            )

            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.onEvent(
                        ProfileEvent.UpdateProfile(
                            nik = nik,
                            phoneNumber = phoneNumber,
                            address = address,
                            bankName = bankName,
                            accountNumber = accountNumber,
                            accountHolderName = accountHolderName,
                            birthdate = birthdate,
                            ktpFile = ktpFile,
                            kkFile = kkFile,
                            npwpFile = npwpFile
                        )
                    )
                },
                enabled = !uiState.isUpdating,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isUpdating) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Simpan Profil")
                }
            }
        }
    }
}

@Composable
fun DocumentUploadItem(
    label: String,
    isUploaded: Boolean,
    isPending: Boolean = false,
    onUpload: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = label, style = MaterialTheme.typography.bodyMedium)
                when {
                    isPending -> {
                        Text(
                            text = "Siap diupload (simpan untuk mengunggah)",
                            color = MaterialTheme.colorScheme.tertiary,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    isUploaded -> {
                        Text(
                            text = "Sudah diupload",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
            IconButton(onClick = onUpload) {
                Icon(Icons.Default.Upload, contentDescription = "Upload $label")
            }
        }
    }
}

// Helper to convert Uri to File
fun uriToFile(context: android.content.Context, uri: Uri): File? {
    try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
        val outputStream = FileOutputStream(tempFile)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}
