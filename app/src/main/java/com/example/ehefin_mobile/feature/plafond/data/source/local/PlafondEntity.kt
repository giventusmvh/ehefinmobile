package com.example.ehefin_mobile.feature.plafond.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity for User Plafond (Credit Limit)
 */
@Entity(tableName = "plafonds")
data class PlafondEntity(
    @PrimaryKey
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
)
