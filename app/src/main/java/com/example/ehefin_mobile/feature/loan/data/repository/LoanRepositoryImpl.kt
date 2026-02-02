package com.example.ehefin_mobile.feature.loan.data.repository

import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.core.di.IoDispatcher
import com.example.ehefin_mobile.core.network.NetworkMonitor
import com.example.ehefin_mobile.feature.loan.data.mapper.toDomain
import com.example.ehefin_mobile.feature.loan.data.mapper.toEntity
import com.example.ehefin_mobile.feature.loan.data.mapper.toLoanItem
import com.example.ehefin_mobile.feature.loan.data.source.local.LoanDao
import com.example.ehefin_mobile.feature.loan.data.source.local.LoanHistoryDao
import com.example.ehefin_mobile.feature.loan.data.source.remote.LoanApi
import com.example.ehefin_mobile.feature.loan.data.source.remote.dto.LoanRequestDto
import com.example.ehefin_mobile.feature.loan.domain.model.LoanApplication
import com.example.ehefin_mobile.feature.loan.domain.model.LoanHistory
import com.example.ehefin_mobile.feature.loan.domain.model.LoanItem
import com.example.ehefin_mobile.feature.loan.domain.model.SubmitLoanRequest
import com.example.ehefin_mobile.feature.loan.domain.repository.LoanRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import retrofit2.Response
import com.example.ehefin_mobile.core.database.dao.PendingRequestDao
import com.example.ehefin_mobile.core.database.entity.PendingRequestEntity
import com.example.ehefin_mobile.core.worker.SyncWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.NetworkType
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Offline-First Repository Implementation for Loans
 * 
 * Pattern: Observable Source of Truth (Room) + Network Sync
 */
class LoanRepositoryImpl @Inject constructor(
    private val loanApi: LoanApi,
    private val loanDao: LoanDao,
    private val loanHistoryDao: LoanHistoryDao,
    private val pendingRequestDao: PendingRequestDao,
    private val workManager: WorkManager,
    private val networkMonitor: NetworkMonitor,
    private val gson: Gson,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LoanRepository {
    
    /**
     * Get all loans with observable pattern
     */
    override fun getLoans(): Flow<Resource<List<LoanItem>>> = flow {
        emit(Resource.Loading())
        
        coroutineScope {
            // 1. Trigger network refresh in background
            launch {
                if (networkMonitor.isOnline()) {
                    try {
                        refreshLoans()
                    } catch (e: Exception) {
                        // Ignore errors here, UI will show cached data
                         // Optionally emit a temporary error via a side channel if needed
                    }
                }
            }
            
            // 2. Observe Database (Source of Truth)
            // This will run indefinitely until the flow is cancelled
            loanDao.getAllLoans().collect { entities ->
                emit(Resource.Success(entities.map { it.toLoanItem() }))
            }
        }
    }.flowOn(ioDispatcher)
    
    /**
     * Get loan detail by ID with observable pattern
     */
    override fun getLoanById(id: Long): Flow<Resource<LoanApplication>> = flow {
        emit(Resource.Loading())
        
        coroutineScope {
            // 1. Trigger network refresh
            launch {
                if (networkMonitor.isOnline()) {
                    try {
                        val response = loanApi.getLoanById(id)
                        if (response.isSuccessful && response.body()?.success == true) {
                            val loanDto = response.body()!!.data!!
                            loanDao.insertLoan(loanDto.toEntity())
                        }
                    } catch (e: Exception) {
                        // Ignore
                    }
                }
            }
            
            // 2. Observe Database
            loanDao.getLoanById(id).collect { entity ->
                if (entity != null) {
                    emit(Resource.Success(entity.toDomain()))
                } else {
                    emit(Resource.Error("Data tidak ditemukan"))
                }
            }
        }
    }.flowOn(ioDispatcher)
    
    /**
     * Submit new loan - requires network
     */
    override suspend fun submitLoan(request: SubmitLoanRequest): Resource<LoanApplication> {
        val requestDto = LoanRequestDto(
            branchId = request.branchId,
            amount = request.amount,
            tenor = request.tenor,
            interestRate = request.interestRate,
            latitude = request.latitude,
            longitude = request.longitude
        )

        if (!networkMonitor.isOnline()) {
            // Offline Mode: Queue the request
            try {
                val jsonRequest = gson.toJson(requestDto)
                val entity = PendingRequestEntity(
                    type = "SUBMIT_LOAN",
                    data = jsonRequest
                )
                pendingRequestDao.insert(entity)
                
                // Schedule Sync
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
                    
                val syncWork = OneTimeWorkRequestBuilder<SyncWorker>()
                    .setConstraints(constraints)
                    .build()
                    
                // Use UniqueWork to avoid duplicate jobs unnecessarily, 
                // but APPEND so they run in sequence if multiple exist
                workManager.enqueueUniqueWork(
                    SyncWorker.WORK_NAME,
                    ExistingWorkPolicy.APPEND_OR_REPLACE,
                    syncWork
                )
                
                // Return dummy success object for UI
                // Note: In a real app, we might want a specific mapping or local ID generation
                return Resource.Success(
                    LoanApplication(
                        id = -1, // Indicates temporary/offline
                        customerId = 0,
                        customerName = "Offline User",
                        customerEmail = "",
                        customerNik = null,
                        customerPhone = null,
                        customerAddress = null,
                        customerBirthdate = null,
                        customerKtpPath = null,
                        customerKkPath = null,
                        customerNpwpPath = null,
                        customerBankName = null,
                        customerAccountNumber = null,
                        customerAccountHolderName = null,
                        productId = 0,
                        productName = "Menunggu Sinkronisasi",
                        branchId = request.branchId,
                        branchName = "Processing...",
                        requestedAmount = request.amount,
                        requestedTenor = request.tenor,
                        requestedRate = request.interestRate,
                        status = com.example.ehefin_mobile.feature.loan.domain.model.LoanStatus.SUBMITTED,
                        createdAt = com.example.ehefin_mobile.core.common.DateUtils.getCurrentDateString(),
                        updatedAt = null
                    )
                )
            } catch (e: Exception) {
                return Resource.Error("Gagal menyimpan data offline: ${e.localizedMessage}")
            }
        }
        
        return try {
            
            val response = loanApi.submitLoan(requestDto)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val loanDto = response.body()!!.data!!
                val entity = loanDto.toEntity()
                
                // Cache the new loan
                loanDao.insertLoan(entity)
                
                Resource.Success(entity.toDomain())
            } else {
                Resource.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            Resource.Error("Gagal mengajukan pinjaman: ${e.localizedMessage}")
        }
    }
    
    /**
     * Get loan history with observable pattern
     */
    override fun getLoanHistory(loanId: Long): Flow<Resource<List<LoanHistory>>> = flow {
        emit(Resource.Loading())
        
        coroutineScope {
            // 1. Trigger network refresh
            launch {
                if (networkMonitor.isOnline()) {
                    try {
                        val response = loanApi.getLoanHistory(loanId)
                        if (response.isSuccessful && response.body()?.success == true) {
                            val historyDtos = response.body()!!.data ?: emptyList()
                            val entities = historyDtos.map { it.toEntity(loanId) }
                            loanHistoryDao.replaceHistoryForLoan(loanId, entities)
                        }
                    } catch (e: Exception) {
                        // Ignore
                    }
                }
            }
            
            // 2. Observe Database
            loanHistoryDao.getHistoryByLoanId(loanId).collect { entities ->
                emit(Resource.Success(entities.map { it.toDomain() }))
            }
        }
    }.flowOn(ioDispatcher)
    
    /**
     * Force refresh loans from network
     */
    override suspend fun refreshLoans(): Resource<Unit> {
        if (!networkMonitor.isOnline()) {
            return Resource.Error("Tidak ada koneksi internet")
        }
        
        return try {
            val response = loanApi.getLoans()
            
            if (response.isSuccessful && response.body()?.success == true) {
                val loanDtos = response.body()!!.data ?: emptyList()
                val entities = loanDtos.map { it.toEntity() }
                loanDao.deleteAllAndInsert(entities)
                Resource.Success(Unit)
            } else {
                Resource.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            Resource.Error("Gagal memperbarui data: ${e.localizedMessage}")
        }
    }
    
    override suspend fun refreshLoanHistory(loanId: Long): Resource<Unit> {
         if (!networkMonitor.isOnline()) {
            return Resource.Error("Tidak ada koneksi internet")
        }
        
        return try {
            val response = loanApi.getLoanHistory(loanId)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val historyDtos = response.body()!!.data ?: emptyList()
                val entities = historyDtos.map { it.toEntity(loanId) }
                loanHistoryDao.replaceHistoryForLoan(loanId, entities)
                Resource.Success(Unit)
            } else {
                Resource.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            Resource.Error("Gagal memperbarui history: ${e.localizedMessage}")
        }
    }
    
    override suspend fun clearCache() {
        loanDao.deleteAllLoans()
        loanHistoryDao.deleteAllHistory()
    }
    
    private fun <T> parseErrorMessage(response: Response<T>): String {
        return try {
            val errorBody = response.errorBody()?.string()
            if (errorBody != null) {
                val jsonObject = gson.fromJson(errorBody, JsonObject::class.java)
                jsonObject.get("message")?.asString ?: "Terjadi kesalahan"
            } else {
                "Terjadi kesalahan"
            }
        } catch (e: Exception) {
            "Terjadi kesalahan"
        }
    }
}