package com.example.ehefin_mobile.feature.loan.domain.model

/**
 * Enum representing all possible loan statuses
 * Matches backend LoanStatus enum
 */
enum class LoanStatus(val displayName: String, val isTerminal: Boolean) {
    SUBMITTED("Diajukan", false),
    MARKETING_APPROVED("Marketing Approved", false),
    MARKETING_REJECTED("Marketing Rejected", true),
    BRANCH_MANAGER_APPROVED("Branch Manager Approved", false),
    BRANCH_MANAGER_REJECTED("Branch Manager Rejected", true),
    DISBURSED("Disetujui", true),
    REJECTED("Ditolak", true);
    
    companion object {
        fun fromString(value: String): LoanStatus {
            return entries.find { it.name == value } ?: SUBMITTED
        }
    }
    
    fun isApproved(): Boolean = this == DISBURSED
    
    fun isRejected(): Boolean = this == MARKETING_REJECTED || 
                                 this == BRANCH_MANAGER_REJECTED || 
                                 this == REJECTED
    
    fun isPending(): Boolean = !isTerminal
}