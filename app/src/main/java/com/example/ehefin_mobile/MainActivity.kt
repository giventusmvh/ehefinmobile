package com.example.ehefin_mobile

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
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
import com.example.ehefin_mobile.navigation.EheFinNavGraph
import com.example.ehefin_mobile.navigation.MainScreenWrapper
import com.example.ehefin_mobile.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint
import android.Manifest
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
        askNotificationPermission()

        enableEdgeToEdge()
        
        setContent {
            val isLoggedIn by tokenManager.isLoggedIn().collectAsState(initial = false)
            
            EheFinTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    
                    // Determine start destination based on login state
                    val startDestination = if (isLoggedIn) {
                        Screen.Home.route
                    } else {
                        Screen.Login.route
                    }
                    
                    MainScreenWrapper(
                        navController = navController,
                        startDestination = startDestination
                    )
                }
            }
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
            startDestination = Screen.Login.route
        )
    }
}
