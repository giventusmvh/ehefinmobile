package com.example.ehefin_mobile.feature.loan.domain.repository

import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.feature.loan.domain.model.LoanApplication
import com.example.ehefin_mobile.feature.loan.domain.model.LoanHistory
import com.example.ehefin_mobile.feature.loan.domain.model.LoanItem
import com.example.ehefin_mobile.feature.loan.domain.model.SubmitLoanRequest
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Loan operations (DIP)
 * Supports offline-first pattern with Flow
 */
interface LoanRepository {
    
    /**
     * Get all loans for current customer
     * Implements offline-first: emit cached -> fetch -> update cache -> emit updated
     */
    fun getLoans(): Flow<Resource<List<LoanItem>>>
    
    /**
     * Get loan detail by ID
     */
    fun getLoanById(id: Long): Flow<Resource<LoanApplication>>
    
    /**
     * Submit new loan application
     */
    suspend fun submitLoan(request: SubmitLoanRequest): Resource<LoanApplication>
    
    /**
     * Get loan approval history
     */
    fun getLoanHistory(loanId: Long): Flow<Resource<List<LoanHistory>>>
    
    /**
     * Refresh loans from remote (force sync)
     */
    suspend fun refreshLoans(): Resource<Unit>
    
    /**
     * Clear local cache
     */
    suspend fun clearCache()
}
