package com.example.ehefin_mobile.feature.loan.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity for Loan - cached for offline-first
 */
@Entity(tableName = "loans")
data class LoanEntity(
    @PrimaryKey
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
    val status: String,
    val createdAt: String,
    val updatedAt: String?,
    val lastSyncedAt: Long = System.currentTimeMillis()
)
