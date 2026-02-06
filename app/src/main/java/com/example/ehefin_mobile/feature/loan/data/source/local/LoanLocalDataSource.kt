package com.example.ehefin_mobile.feature.loan.data.source.local

import com.example.ehefin_mobile.core.common.DataResult
import kotlinx.coroutines.flow.Flow

/**
 * Interface for Loan local data source operations.
 * Abstracts Room DAO calls for testability and separation of concerns.
 */
interface LoanLocalDataSource {

    /**
     * Get all loans as Flow for reactive updates.
     */
    fun getAllLoansFlow(): Flow<List<LoanEntity>>

    /**
     * Get loan by ID as Flow for reactive updates.
     */
    fun getLoanByIdFlow(loanId: Long): Flow<LoanEntity?>

    /**
     * Get loan by ID synchronously.
     */
    suspend fun getLoanById(loanId: Long): DataResult<LoanEntity?>

    /**
     * Save a single loan.
     */
    suspend fun saveLoan(loan: LoanEntity): DataResult<Unit>

    /**
     * Save multiple loans.
     */
    suspend fun saveLoans(loans: List<LoanEntity>): DataResult<Unit>

    /**
     * Replace all loans with new data.
     */
    suspend fun replaceAllLoans(loans: List<LoanEntity>): DataResult<Unit>

    /**
     * Delete all loans.
     */
    suspend fun deleteAllLoans(): DataResult<Unit>

    /**
     * Get all branches as Flow.
     */
    fun getAllBranchesFlow(): Flow<List<BranchEntity>>

    /**
     * Save branches.
     */
    suspend fun saveBranches(branches: List<BranchEntity>): DataResult<Unit>

    /**
     * Get loan history as Flow.
     */
    fun getLoanHistoryFlow(loanId: Long): Flow<List<LoanHistoryEntity>>

    /**
     * Save loan history.
     */
    suspend fun saveLoanHistory(history: List<LoanHistoryEntity>): DataResult<Unit>
}
