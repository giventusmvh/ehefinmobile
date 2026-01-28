package com.example.ehefin_mobile.feature.plafond.domain.model

/** Domain model for Product (Plafond tier/type) */
data class Product(
        val id: Long,
        val name: String,
        val amount: Double,
        val tenor: Int,
        val interestRate: Double
)