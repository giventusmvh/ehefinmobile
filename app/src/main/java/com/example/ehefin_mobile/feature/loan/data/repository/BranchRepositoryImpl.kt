package com.example.ehefin_mobile.feature.loan.data.repository

import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.core.di.IoDispatcher
import com.example.ehefin_mobile.core.network.NetworkMonitor
import com.example.ehefin_mobile.feature.loan.data.mapper.toDomain
import com.example.ehefin_mobile.feature.loan.data.mapper.toEntity
import com.example.ehefin_mobile.feature.loan.data.source.local.BranchDao
import com.example.ehefin_mobile.feature.loan.data.source.remote.LoanApi
import com.example.ehefin_mobile.feature.loan.domain.model.Branch
import com.example.ehefin_mobile.feature.loan.domain.repository.BranchRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Offline-First Repository Implementation for Branches
 */
class BranchRepositoryImpl @Inject constructor(
    private val loanApi: LoanApi,
    private val branchDao: BranchDao,
    private val networkMonitor: NetworkMonitor,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BranchRepository {
    
    override fun getBranches(): Flow<Resource<List<Branch>>> = flow {
        emit(Resource.Loading())
        
        // 1. Emit cached data first
        val cachedBranches = branchDao.getAllBranches().first()
        if (cachedBranches.isNotEmpty()) {
            emit(Resource.Success(cachedBranches.map { it.toDomain() }))
        }
        
        // 2. Fetch from network
        if (networkMonitor.isOnline()) {
            try {
                val response = loanApi.getBranches()
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val branchDtos = response.body()!!.data ?: emptyList()
                    val entities = branchDtos.map { it.toEntity() }
                    
                    // 3. Update cache
                    branchDao.deleteAllAndInsert(entities)
                    
                    // 4. Emit fresh data
                    emit(Resource.Success(entities.map { it.toDomain() }))
                } else {
                    if (cachedBranches.isEmpty()) {
                        emit(Resource.Error("Gagal memuat daftar cabang"))
                    }
                }
            } catch (e: Exception) {
                if (cachedBranches.isEmpty()) {
                    emit(Resource.Error("Tidak dapat terhubung ke server"))
                }
            }
        } else {
            if (cachedBranches.isEmpty()) {
                emit(Resource.Error("Tidak ada koneksi internet"))
            }
        }
    }.flowOn(ioDispatcher)
    
    override suspend fun refreshBranches(): Resource<Unit> {
        if (!networkMonitor.isOnline()) {
            return Resource.Error("Tidak ada koneksi internet")
        }
        
        return try {
            val response = loanApi.getBranches()
            
            if (response.isSuccessful && response.body()?.success == true) {
                val branchDtos = response.body()!!.data ?: emptyList()
                val entities = branchDtos.map { it.toEntity() }
                branchDao.deleteAllAndInsert(entities)
                Resource.Success(Unit)
            } else {
                Resource.Error("Gagal memperbarui data cabang")
            }
        } catch (e: Exception) {
            Resource.Error("Gagal memperbarui data: ${e.localizedMessage}")
        }
    }
}