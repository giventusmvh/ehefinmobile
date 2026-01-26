package com.example.ehefin_mobile.feature.plafond.domain.model

/** Domain model for User Plafond (Credit Limit) */
data class UserPlafond(
        val id: Long,
        val userId: Long,
        val productId: Long,
        val productName: String,
        val originalAmount: Double,
        val remainingAmount: Double,
        val tenor: Int,
        val interestRate: Double,
        val assignedAt: String,
        val isActive: Boolean
) {
    val usedAmount: Double
        get() = originalAmount - remainingAmount

    val usagePercentage: Float
        get() = if (originalAmount > 0) ((usedAmount / originalAmount) * 100).toFloat() else 0f
}
