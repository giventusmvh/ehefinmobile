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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import retrofit2.Response
import javax.inject.Inject

/**
 * Offline-First Repository Implementation for Loans
 * 
 * Pattern:
 * 1. Emit Loading state
 * 2. Emit cached data from Room (if available)
 * 3. Fetch fresh data from API
 * 4. Update Room cache
 * 5. Emit updated data
 * 
 * Benefits:
 * - Instant UI response from cache
 * - Always fresh data when online
 * - Works offline with cached data
 */
class LoanRepositoryImpl @Inject constructor(
    private val loanApi: LoanApi,
    private val loanDao: LoanDao,
    private val loanHistoryDao: LoanHistoryDao,
    private val networkMonitor: NetworkMonitor,
    private val gson: Gson,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LoanRepository {
    
    /**
     * Get all loans with offline-first pattern
     */
    override fun getLoans(): Flow<Resource<List<LoanItem>>> = flow {
        emit(Resource.Loading())
        
        // 1. Emit cached data first (for instant UI)
        val cachedLoans = loanDao.getAllLoans().first()
        if (cachedLoans.isNotEmpty()) {
            emit(Resource.Success(cachedLoans.map { it.toLoanItem() }))
        }
        
        // 2. Fetch from network if online
        if (networkMonitor.isOnline()) {
            try {
                val response = loanApi.getLoans()
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val loanDtos = response.body()!!.data ?: emptyList()
                    val entities = loanDtos.map { it.toEntity() }
                    
                    // 3. Update cache atomically
                    loanDao.deleteAllAndInsert(entities)
                    
                    // 4. Emit fresh data
                    emit(Resource.Success(entities.map { it.toLoanItem() }))
                } else {
                    // API error but we have cache
                    val errorMsg = parseErrorMessage(response)
                    if (cachedLoans.isNotEmpty()) {
                        emit(Resource.Error(errorMsg, cachedLoans.map { it.toLoanItem() }))
                    } else {
                        emit(Resource.Error(errorMsg))
                    }
                }
            } catch (e: Exception) {
                // Network error but we have cache
                val errorMsg = "Tidak dapat terhubung ke server: ${e.localizedMessage}"
                if (cachedLoans.isNotEmpty()) {
                    emit(Resource.Error(errorMsg, cachedLoans.map { it.toLoanItem() }))
                } else {
                    emit(Resource.Error(errorMsg))
                }
            }
        } else {
            // Offline mode
            if (cachedLoans.isEmpty()) {
                emit(Resource.Error("Tidak ada koneksi internet dan tidak ada data tersimpan"))
            }
            // If we already emitted cached data, just stay with that
        }
    }.flowOn(ioDispatcher)
    
    /**
     * Get loan detail by ID with offline-first pattern
     */
    override fun getLoanById(id: Long): Flow<Resource<LoanApplication>> = flow {
        emit(Resource.Loading())
        
        // 1. Emit cached data first
        val cachedLoan = loanDao.getLoanByIdSync(id)
        if (cachedLoan != null) {
            emit(Resource.Success(cachedLoan.toDomain()))
        }
        
        // 2. Fetch from network
        if (networkMonitor.isOnline()) {
            try {
                val response = loanApi.getLoanById(id)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val loanDto = response.body()!!.data!!
                    val entity = loanDto.toEntity()
                    
                    // 3. Update cache
                    loanDao.insertLoan(entity)
                    
                    // 4. Emit fresh data
                    emit(Resource.Success(entity.toDomain()))
                } else {
                    val errorMsg = parseErrorMessage(response)
                    if (cachedLoan != null) {
                        emit(Resource.Error(errorMsg, cachedLoan.toDomain()))
                    } else {
                        emit(Resource.Error(errorMsg))
                    }
                }
            } catch (e: Exception) {
                val errorMsg = "Tidak dapat terhubung ke server"
                if (cachedLoan != null) {
                    emit(Resource.Error(errorMsg, cachedLoan.toDomain()))
                } else {
                    emit(Resource.Error(errorMsg))
                }
            }
        } else {
            if (cachedLoan == null) {
                emit(Resource.Error("Tidak ada koneksi internet dan data tidak tersedia"))
            }
        }
    }.flowOn(ioDispatcher)
    
    /**
     * Submit new loan - requires network
     */
    override suspend fun submitLoan(request: SubmitLoanRequest): Resource<LoanApplication> {
        if (!networkMonitor.isOnline()) {
            return Resource.Error("Tidak ada koneksi internet. Silakan coba lagi.")
        }
        
        return try {
            val requestDto = LoanRequestDto(
                branchId = request.branchId,
                amount = request.amount,
                tenor = request.tenor,
                interestRate = request.interestRate
            )
            
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
     * Get loan history with offline-first pattern
     */
    override fun getLoanHistory(loanId: Long): Flow<Resource<List<LoanHistory>>> = flow {
        emit(Resource.Loading())
        
        // 1. Emit cached history first
        val cachedHistory = loanHistoryDao.getHistoryByLoanId(loanId).first()
        if (cachedHistory.isNotEmpty()) {
            emit(Resource.Success(cachedHistory.map { it.toDomain() }))
        }
        
        // 2. Fetch from network
        if (networkMonitor.isOnline()) {
            try {
                val response = loanApi.getLoanHistory(loanId)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val historyDtos = response.body()!!.data ?: emptyList()
                    val entities = historyDtos.map { it.toEntity(loanId) }
                    
                    // 3. Update cache
                    loanHistoryDao.replaceHistoryForLoan(loanId, entities)
                    
                    // 4. Emit fresh data
                    emit(Resource.Success(entities.map { it.toDomain() }))
                } else {
                    val errorMsg = parseErrorMessage(response)
                    if (cachedHistory.isNotEmpty()) {
                        emit(Resource.Error(errorMsg, cachedHistory.map { it.toDomain() }))
                    } else {
                        emit(Resource.Error(errorMsg))
                    }
                }
            } catch (e: Exception) {
                val errorMsg = "Tidak dapat terhubung ke server"
                if (cachedHistory.isNotEmpty()) {
                    emit(Resource.Error(errorMsg, cachedHistory.map { it.toDomain() }))
                } else {
                    emit(Resource.Error(errorMsg))
                }
            }
        } else {
            if (cachedHistory.isEmpty()) {
                emit(Resource.Error("Tidak ada koneksi internet dan tidak ada history tersimpan"))
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
