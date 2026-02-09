package com.example.ehefin_mobile

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.ehefin_mobile.core.datastore.TokenManager
import com.example.ehefin_mobile.core.designsystem.theme.EheFinTheme
import com.example.ehefin_mobile.core.security.RootDetector
import com.example.ehefin_mobile.navigation.EheFinNavGraph
import com.example.ehefin_mobile.navigation.MainScreenWrapper
import com.example.ehefin_mobile.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint
import android.Manifest
import android.util.Log
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var tokenManager: TokenManager

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        Boolean ->
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Security: Prevent screen capture, screenshot, and screen recording
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        
        // Security: Check for rooted device
        checkRootedDevice()
        
        askNotificationPermission()

        enableEdgeToEdge()
        
        setContent {
            val isLoggedIn by tokenManager.isLoggedIn().collectAsState(initial = false)
            
            EheFinTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    
                    // Always start at Home, let the screen handle the state
                    val startDestination = Screen.Home.route
                    
                    MainScreenWrapper(
                        navController = navController,
                        startDestination = startDestination,
                        isLoggedIn = isLoggedIn
                    )
                }
            }
        }
    }
    
    /**
     * Check if device is rooted and show warning dialog
     */
    private fun checkRootedDevice() {
        if (RootDetector.isDeviceRooted(this)) {
            Log.w("Security", "Rooted device detected!")
            Log.w("Security", RootDetector.getDetectionDetails(this).toString())
            
            AlertDialog.Builder(this)
                .setTitle("Peringatan Keamanan")
                .setMessage(
                    "Perangkat Anda terdeteksi sudah di-root. " +
                    "Menggunakan aplikasi ini pada perangkat yang di-root " +
                    "dapat menimbulkan risiko keamanan terhadap data finansial Anda.\n\n" +
                    "Kami sangat menyarankan untuk menggunakan perangkat yang tidak di-root."
                )
                .setCancelable(false)
                .setPositiveButton("Keluar") { _, _ ->
                    finish()
                }
                .setNegativeButton("Lanjutkan dengan Risiko") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun askNotificationPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                ){
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    EheFinTheme {
        val navController = rememberNavController()
        EheFinNavGraph(
            navController = navController,
            startDestination = Screen.Login.route,
            isLoggedIn = false
        )
    }
}