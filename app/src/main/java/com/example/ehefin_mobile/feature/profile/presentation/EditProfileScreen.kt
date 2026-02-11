package com.example.ehefin_mobile.feature.profile.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ehefin_mobile.R
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.ehefin_mobile.core.util.ImageCompressionUtil

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
    val scope = rememberCoroutineScope()

    // State for form fields
    var nik by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var accountHolderName by remember { mutableStateOf("") }
    var birthdate by remember { mutableStateOf("") } // YYYY-MM-DD format for simplicity
    var job by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }

    // State for document files
    var ktpFile by remember { mutableStateOf<File?>(null) }
    var kkFile by remember { mutableStateOf<File?>(null) }
    var npwpFile by remember { mutableStateOf<File?>(null) }
    var selfieFile by remember { mutableStateOf<File?>(null) }
    var salarySlipFile by remember { mutableStateOf<File?>(null) }

    // Bottom sheet state
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

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
            if (job.isEmpty()) job = it.job ?: ""
            if (companyName.isEmpty()) companyName = it.companyName ?: ""
        }
    }

    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            snackbarHostState.showSnackbar(context.getString(R.string.edit_profile_saved))
            navController.navigateUp()
        }
    }

    // Using a map to track which doc is being uploaded
    var activeUploadType by remember { mutableStateOf<String?>(null) }
    
    // Camera URI state
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCameraFile by remember { mutableStateOf<File?>(null) }
    
    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            activeUploadType?.let { type ->
                val file = uriToFile(context, selectedUri)
                if (file != null) {
                    val compressedFile = ImageCompressionUtil.compressImage(context, file)
                    when (type) {
                        "KTP" -> ktpFile = compressedFile
                        "KK" -> kkFile = compressedFile
                        "NPWP" -> npwpFile = compressedFile
                        "SELFIE" -> selfieFile = compressedFile
                        "SALARY_SLIP" -> salarySlipFile = compressedFile
                    }
                }
            }
        }
    }
    
    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            pendingCameraFile?.let { file ->
                val compressedFile = ImageCompressionUtil.compressImage(context, file)
                activeUploadType?.let { type ->
                    when (type) {
                        "KTP" -> ktpFile = compressedFile
                        "KK" -> kkFile = compressedFile
                        "NPWP" -> npwpFile = compressedFile
                        "SELFIE" -> selfieFile = compressedFile
                        "SALARY_SLIP" -> salarySlipFile = compressedFile
                    }
                }
            }
        }
        pendingCameraFile = null
        cameraImageUri = null
    }
    
    // Camera permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Create file and launch camera
            val imageFile = createImageFile(context)
            pendingCameraFile = imageFile
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                imageFile
            )
            cameraImageUri = uri
            cameraLauncher.launch(uri)
        } else {
            scope.launch {
                snackbarHostState.showSnackbar(context.getString(R.string.edit_profile_camera_permission))
            }
        }
    }
    
    // Function to launch camera with permission check
    fun launchCamera() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, 
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        
        if (hasPermission) {
            val imageFile = createImageFile(context)
            pendingCameraFile = imageFile
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                imageFile
            )
            cameraImageUri = uri
            cameraLauncher.launch(uri)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    
    // Function to launch gallery
    fun launchGallery() {
        galleryLauncher.launch("image/*")
    }
    
    // Function to show bottom sheet for upload options
    fun showUploadOptions(docType: String) {
        activeUploadType = docType
        showBottomSheet = true
    }

    // Bottom sheet for image source picker
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            ImageSourcePickerSheet(
                onCameraClick = {
                    showBottomSheet = false
                    launchCamera()
                },
                onGalleryClick = {
                    showBottomSheet = false
                    launchGallery()
                }
            )
        }
    }

    // Date Picker Dialog
    // Calculate max date (18 years ago)
    val maxDateMillis = remember {
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.YEAR, -18)
        calendar.timeInMillis
    }

    val datePickerState = rememberDatePickerState(
        initialDisplayedMonthMillis = maxDateMillis,
        selectableDates = object : androidx.compose.material3.SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= maxDateMillis
            }
        }
    )
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Date(millis)
                        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        birthdate = format.format(date)
                    }
                    showDatePicker = false
                }) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_profile)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
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
                label = { Text(stringResource(R.string.edit_profile_nik)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = birthdate,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text(stringResource(R.string.edit_profile_birthdate)) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = stringResource(R.string.select_date)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showDatePicker = true }
                )
            }

            OutlinedTextField(
                value = job,
                onValueChange = { job = it },
                label = { Text(stringResource(R.string.edit_profile_job)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = companyName,
                onValueChange = { companyName = it },
                label = { Text(stringResource(R.string.edit_profile_company)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text(stringResource(R.string.edit_profile_phone)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text(stringResource(R.string.edit_profile_address)) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            SectionHeader(title = stringResource(R.string.edit_profile_bank_section))

            OutlinedTextField(
                value = bankName,
                onValueChange = { bankName = it },
                label = { Text(stringResource(R.string.edit_profile_bank_name)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = accountNumber,
                onValueChange = { if (it.length <= 20) accountNumber = it },
                label = { Text(stringResource(R.string.edit_profile_account_number)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = accountHolderName,
                onValueChange = { accountHolderName = it },
                label = { Text(stringResource(R.string.edit_profile_account_holder)) },
                modifier = Modifier.fillMaxWidth()
            )

            SectionHeader(title = stringResource(R.string.edit_profile_doc_section))

            DocumentUploadItem(
                label = stringResource(R.string.edit_profile_ktp),
                isUploaded = ktpFile != null || !uiState.profile?.ktpPath.isNullOrEmpty(),
                isPending = ktpFile != null,
                file = ktpFile,
                imageUrl = uiState.profile?.ktpPath,
                accessToken = uiState.accessToken,
                onUpload = { showUploadOptions("KTP") }
            )

            DocumentUploadItem(
                label = stringResource(R.string.edit_profile_kk),
                isUploaded = kkFile != null || !uiState.profile?.kkPath.isNullOrEmpty(),
                isPending = kkFile != null,
                file = kkFile,
                imageUrl = uiState.profile?.kkPath,
                accessToken = uiState.accessToken,
                onUpload = { showUploadOptions("KK") }
            )

            DocumentUploadItem(
                label = stringResource(R.string.edit_profile_npwp),
                isUploaded = npwpFile != null || !uiState.profile?.npwpPath.isNullOrEmpty(),
                isPending = npwpFile != null,
                file = npwpFile,
                imageUrl = uiState.profile?.npwpPath,
                accessToken = uiState.accessToken,
                onUpload = { showUploadOptions("NPWP") }
            )

            DocumentUploadItem(
                label = stringResource(R.string.edit_profile_selfie),
                isUploaded = selfieFile != null || !uiState.profile?.selfiePath.isNullOrEmpty(),
                isPending = selfieFile != null,
                file = selfieFile,
                imageUrl = uiState.profile?.selfiePath,
                accessToken = uiState.accessToken,
                onUpload = { showUploadOptions("SELFIE") }
            )

            DocumentUploadItem(
                label = stringResource(R.string.edit_profile_salary_slip),
                isUploaded = salarySlipFile != null || !uiState.profile?.salarySlipPath.isNullOrEmpty(),
                isPending = salarySlipFile != null,
                file = salarySlipFile,
                imageUrl = uiState.profile?.salarySlipPath,
                accessToken = uiState.accessToken,
                onUpload = { showUploadOptions("SALARY_SLIP") }
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
                            job = job,
                            companyName = companyName,
                            ktpFile = ktpFile,
                            kkFile = kkFile,
                            npwpFile = npwpFile,
                            selfieFile = selfieFile,
                            salarySlipFile = salarySlipFile
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
                    Text(stringResource(R.string.edit_profile_save))
                }
            }
        }
    }
}

@Composable
fun ImageSourcePickerSheet(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.edit_profile_image_source),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCameraClick() }
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = stringResource(R.string.edit_profile_take_photo),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = stringResource(R.string.edit_profile_take_photo_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        HorizontalDivider()
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onGalleryClick() }
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = stringResource(R.string.edit_profile_gallery),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = stringResource(R.string.edit_profile_gallery_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun DocumentUploadItem(
    label: String,
    isUploaded: Boolean,
    isPending: Boolean = false,
    file: File? = null,
    imageUrl: String? = null,
    accessToken: String? = null,
    onUpload: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
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
                                text = stringResource(R.string.edit_profile_doc_pending),
                                color = MaterialTheme.colorScheme.tertiary,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        isUploaded -> {
                            Text(
                                text = stringResource(R.string.edit_profile_doc_uploaded),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
                IconButton(onClick = onUpload) {
                    Icon(Icons.Default.Upload, contentDescription = stringResource(R.string.edit_profile_upload_label, label))
                }
            }
            
            // Show preview if available (either file or url)
            if (file != null || !imageUrl.isNullOrEmpty()) {
                val context = LocalContext.current
                var model: Any? = file
                
                if (file == null && !imageUrl.isNullOrEmpty()) {
                    model = if (accessToken != null) {
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
                
                if (model != null) {
                    AsyncImage(
                        model = model,
                        contentDescription = stringResource(R.string.edit_profile_preview_label, label),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

// Helper to create image file for camera
fun createImageFile(context: android.content.Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_${timeStamp}_"
    return File.createTempFile(imageFileName, ".jpg", context.cacheDir)
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

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
    HorizontalDivider()
}