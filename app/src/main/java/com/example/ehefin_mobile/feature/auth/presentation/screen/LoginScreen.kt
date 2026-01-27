package com.example.ehefin_mobile.feature.auth.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ehefin_mobile.feature.auth.presentation.viewmodel.AuthEvent
import com.example.ehefin_mobile.feature.auth.presentation.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

@Composable
fun LoginScreen(
        onLoginSuccess: () -> Unit,
        onNavigateToRegister: () -> Unit,
        onNavigateToForgotPassword: () -> Unit,
        viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle events
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is AuthEvent.LoginSuccess -> onLoginSuccess()
                is AuthEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                else -> {}
            }
        }
    }

    // Show error in snackbar
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                    modifier =
                            Modifier.fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                                    .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(48.dp))

                // Logo placeholder - can be replaced with actual logo
                Box(
                        modifier = Modifier.size(140.dp).padding(16.dp),
                        contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "EheFin",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                }


                Spacer(modifier = Modifier.height(16.dp))

                Text(
                        text = "Selamat Datang!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                        text = "Masuk ke akun Anda untuk melanjutkan",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Email field
                androidx.compose.material3.OutlinedTextField(
                        value = uiState.loginEmail,
                        onValueChange = viewModel::onLoginEmailChange,
                        label = { Text("Email") },
                        placeholder = { Text("contoh@email.com") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions =
                                androidx.compose.foundation.text.KeyboardOptions(
                                        keyboardType = KeyboardType.Email,
                                        imeAction = ImeAction.Next
                                ),
                        leadingIcon = {
                            Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password field
                var passwordVisible by remember { androidx.compose.runtime.mutableStateOf(false) }
                androidx.compose.material3.OutlinedTextField(
                        value = uiState.loginPassword,
                        onValueChange = viewModel::onLoginPasswordChange,
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                            onDone = { viewModel.login() }
                        ),
                        trailingIcon = {
                            androidx.compose.material3.IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) androidx.compose.material.icons.Icons.Filled.Visibility else androidx.compose.material.icons.Icons.Filled.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                )
                            }
                        }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Forgot password
                Box(modifier = Modifier.fillMaxWidth()) {
                    androidx.compose.material3.TextButton(
                            onClick = onNavigateToForgotPassword,
                            modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Text("Lupa Password?")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Login button
                androidx.compose.material3.Button(
                        onClick = viewModel::login,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = !uiState.isLoading && uiState.loginEmail.isNotBlank() && uiState.loginPassword.isNotBlank()
                ) {
                    if (uiState.isLoading) {
                        androidx.compose.material3.CircularProgressIndicator(
                            color = androidx.compose.ui.graphics.Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Masuk")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Register link
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                            text = "Belum punya akun?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    androidx.compose.material3.TextButton(onClick = onNavigateToRegister) {
                        Text("Daftar Sekarang")
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}
