package com.example.ehefin_mobile.core.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext

interface LocationHelper {
    suspend fun getCurrentLocation(): Location?
}


@Singleton
class LocationHelperImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationHelper {
    
    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Location? {
        // Assume permission is already granted when calling this
        return try {
            // First try to get last known location (fast, cached)
            var location = fusedLocationClient.lastLocation.await()
            
            // If no cached location, try to get current location
            if (location == null) {
                val priority = Priority.PRIORITY_HIGH_ACCURACY
                location = fusedLocationClient.getCurrentLocation(
                    priority,
                    CancellationTokenSource().token
                ).await()
            }
            location
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
