package com.example.ehefin_mobile.core.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.ehefin_mobile.feature.loan.domain.repository.BranchRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class ReferenceDataWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val branchRepository: BranchRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Refresh branches
            val result = branchRepository.refreshBranches()
            
            // Add other reference data refreshes here (Products, Plafonds, etc.)
            
            if (result is com.example.ehefin_mobile.core.common.Resource.Success) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
    
    companion object {
        const val WORK_NAME = "reference_data_work"
        const val TYPE_BRANCHES = "branches"
    }
}