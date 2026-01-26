package com.example.ehefin_mobile.feature.plafond.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity for Product
 */
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val amount: Double,
    val tenor: Int,
    val interestRate: Double
)
