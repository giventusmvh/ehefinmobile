package com.example.ehefin_mobile.feature.loan.domain.model

/**
 * Domain model for Loan History entry
 * Represents approval/rejection actions on a loan
 */
data class LoanHistory(
    val id: Long,
    val loanId: Long,
    val status: LoanStatus,
    val note: String?,
    val approvedBy: String?,
    val approvedByRole: String?,
    val approvedByBranchName: String?,
    val createdAt: String
)