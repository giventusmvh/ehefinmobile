package com.example.ehefin_mobile.feature.loan.domain.repository

import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.feature.loan.domain.model.Branch
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Branch operations (DIP)
 */
interface BranchRepository {
    
    /**
     * Get all branches (offline-first)
     */
    fun getBranches(): Flow<Resource<List<Branch>>>
    
    /**
     * Refresh branches from remote
     */
    suspend fun refreshBranches(): Resource<Unit>
}