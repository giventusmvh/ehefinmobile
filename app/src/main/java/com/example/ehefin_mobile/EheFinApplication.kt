package com.example.ehefin_mobile

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class EheFinApplication : Application(), Configuration.Provider {
    
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
            
    override fun onCreate() {
        super.onCreate()
        scheduleSyncs()
    }
    
    private fun scheduleSyncs() {
        // 1. Recurring: Reference Data (Branches, etc.)
        val referenceDataWorkRequest = androidx.work.PeriodicWorkRequestBuilder<com.example.ehefin_mobile.core.worker.ReferenceDataWorker>(
            24, java.util.concurrent.TimeUnit.HOURS
        )
            .setConstraints(
                androidx.work.Constraints.Builder()
                    .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                    .build()
            )
            .build()

        androidx.work.WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "reference_data_sync",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            referenceDataWorkRequest
        )
        
        // 2. Immediate: Reference Data (for fresh start)
        val oneTimeReferenceRequest = androidx.work.OneTimeWorkRequestBuilder<com.example.ehefin_mobile.core.worker.ReferenceDataWorker>()
            .setConstraints(
                androidx.work.Constraints.Builder()
                    .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                    .build()
            )
            .build()
            
        androidx.work.WorkManager.getInstance(this).enqueueUniqueWork(
            "reference_data_sync_immediate",
            androidx.work.ExistingWorkPolicy.KEEP,
            oneTimeReferenceRequest
        )
        
        // 3. Immediate: Pending Requests (Uploads)
        // This ensures if a previous sync failed or app was killed, we retry immediately on launch
        val syncRequest = androidx.work.OneTimeWorkRequestBuilder<com.example.ehefin_mobile.core.worker.SyncWorker>()
            .setConstraints(
                androidx.work.Constraints.Builder()
                    .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                    .build()
            )
            .build()
            
        androidx.work.WorkManager.getInstance(this).enqueueUniqueWork(
            "sync_pending_requests_startup",
            androidx.work.ExistingWorkPolicy.KEEP,
            syncRequest
        )
    }
}
