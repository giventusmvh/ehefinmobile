package com.example.ehefin_mobile.feature.plafond.data.source.remote.dto

import com.google.gson.annotations.SerializedName

data class ProductResponseDto(
        @SerializedName("id") val id: Long,
        @SerializedName("name") val name: String,
        @SerializedName("amount") val amount: Double,
        @SerializedName("tenor") val tenor: Int,
        @SerializedName("interestRate") val interestRate: Double
)

data class PlafondResponseDto(
        @SerializedName("id") val id: Long,
        @SerializedName("product") val product: ProductResponseDto,
        @SerializedName("originalAmount") val originalAmount: Double,
        @SerializedName("remainingAmount") val remainingAmount: Double,
        @SerializedName("assignedAt") val assignedAt: String,
        @SerializedName("isActive") val isActive: Boolean
)

data class SelectPlafondRequest(@SerializedName("productId") val productId: Long)
