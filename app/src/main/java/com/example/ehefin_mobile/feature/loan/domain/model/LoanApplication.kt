package com.example.ehefin_mobile.feature.loan.domain.model

/**
 * Domain model for Loan Application
 * Contains snapshot data as submitted
 */
data class LoanApplication(
    val id: Long,
    val customerId: Long,
    val customerName: String,
    val customerEmail: String,
    val customerNik: String?,
    val customerPhone: String?,
    val customerAddress: String?,
    val customerBirthdate: String?,
    val customerKtpPath: String?,
    val customerKkPath: String?,
    val customerNpwpPath: String?,
    val customerBankName: String?,
    val customerAccountNumber: String?,
    val customerAccountHolderName: String?,
    val productId: Long,
    val productName: String,
    val branchId: Long,
    val branchName: String,
    val requestedAmount: Double,
    val requestedTenor: Int,
    val requestedRate: Double,
    val status: LoanStatus,
    val createdAt: String,
    val updatedAt: String?
)

/**
 * Simplified loan item for list display
 */
data class LoanItem(
    val id: Long,
    val customerName: String,
    val productName: String,
    val branchName: String,
    val amount: Double,
    val tenor: Int,
    val interestRate: Double,
    val status: LoanStatus,
    val createdAt: String
)

/**
 * Request model for submitting a new loan
 */
data class SubmitLoanRequest(
    val branchId: Long,
    val amount: Double,
    val tenor: Int,
    val interestRate: Double,
    val latitude: String? = null,
    val longitude: String? = null
)