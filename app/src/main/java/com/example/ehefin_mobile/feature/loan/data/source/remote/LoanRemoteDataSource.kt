package com.example.ehefin_mobile.feature.loan.data.source.remote

import com.example.ehefin_mobile.core.common.DataResult
import com.example.ehefin_mobile.feature.loan.data.source.remote.dto.BranchDto
import com.example.ehefin_mobile.feature.loan.data.source.remote.dto.LoanHistoryDto
import com.example.ehefin_mobile.feature.loan.data.source.remote.dto.LoanRequestDto
import com.example.ehefin_mobile.feature.loan.data.source.remote.dto.LoanResponseDto

/**
 * Interface for Loan remote data source operations.
 * Abstracts API calls for testability and separation of concerns.
 */
interface LoanRemoteDataSource {

    /**
     * Get all loans for current user.
     */
    suspend fun getLoans(): DataResult<List<LoanResponseDto>>

    /**
     * Get loan detail by ID.
     */
    suspend fun getLoanById(loanId: Long): DataResult<LoanResponseDto>

    /**
     * Submit a new loan application.
     */
    suspend fun submitLoan(request: LoanRequestDto): DataResult<LoanResponseDto>

    /**
     * Get loan history (status changes) for a specific loan.
     */
    suspend fun getLoanHistory(loanId: Long): DataResult<List<LoanHistoryDto>>

    /**
     * Get all available branches.
     */
    suspend fun getBranches(): DataResult<List<BranchDto>>
}