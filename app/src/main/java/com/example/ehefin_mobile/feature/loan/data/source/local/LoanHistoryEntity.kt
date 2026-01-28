package com.example.ehefin_mobile.feature.loan.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity for Loan History
 */
@Entity(tableName = "loan_history")
data class LoanHistoryEntity(
    @PrimaryKey
    val id: Long,
    val loanId: Long,
    val status: String,
    val note: String?,
    val approvedBy: String?,
    val approvedByRole: String?,
    val approvedByBranchName: String?,
    val createdAt: String
)