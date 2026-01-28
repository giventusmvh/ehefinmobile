package com.example.ehefin_mobile.core.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.ehefin_mobile.core.database.dao.PendingRequestDao
import com.example.ehefin_mobile.feature.loan.data.source.remote.LoanApi
import com.example.ehefin_mobile.feature.loan.data.source.remote.dto.LoanRequestDto
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import android.util.Log

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val pendingRequestDao: PendingRequestDao,
    private val loanApi: LoanApi,
    private val gson: Gson
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d(WORK_NAME, "Worker started. Checking for pending requests...")
        val pendingRequests = pendingRequestDao.getAllRequests()
        Log.d(WORK_NAME, "Found ${pendingRequests.size} pending requests.")

        if (pendingRequests.isEmpty()) {
            return@withContext Result.success()
        }

        var successCount = 0
        var failCount = 0

        for (request in pendingRequests) {
            try {
                Log.d(WORK_NAME, "Processing request ID: ${request.id}, Type: ${request.type}")
                if (processRequest(request)) {
                    Log.d(WORK_NAME, "Request ID: ${request.id} SUCCESS")
                    pendingRequestDao.delete(request)
                    successCount++
                } else {
                    Log.e(WORK_NAME, "Request ID: ${request.id} FAILED")
                    failCount++
                }
            } catch (e: Exception) {
                Log.e(WORK_NAME, "Request ID: ${request.id} EXCEPTION: ${e.message}")
                e.printStackTrace()
                failCount++
            }
        }

        if (failCount > 0) {
            // If some failed, retry later
            Result.retry()
        } else {
            Result.success()
        }
    }

    private suspend fun processRequest(request: com.example.ehefin_mobile.core.database.entity.PendingRequestEntity): Boolean {
        return when (request.type) {
            "SUBMIT_LOAN" -> {
                val loanRequest = gson.fromJson(request.data, LoanRequestDto::class.java)
                val response = loanApi.submitLoan(loanRequest)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    true
                } else if (response.code() == 400) {
                    // Start of User Request Implementation
                    // If 400 (Bad Request), e.g., duplicate loan, we should drop this request
                    // so it doesn't block/retry forever.
                    Log.w(WORK_NAME, "Request rejected with 400 (likely duplicate). Deleting request. Error: ${response.message()}")
                    true
                } else {
                    false
                }
            }
            else -> true // Unknown type, just delete? or keep? deleting for now to avoid loop
        }
    }
    
    companion object {
        const val WORK_NAME = "sync_work"
    }
}