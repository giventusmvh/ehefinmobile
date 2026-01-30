package com.example.ehefin_mobile.feature.loan.data.source.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Request body for submitting new loan
 */
data class LoanRequestDto(
    @SerializedName("branchId")
    val branchId: Long,
    
    @SerializedName("amount")
    val amount: Double,
    
    @SerializedName("tenor")
    val tenor: Int,
    
    @SerializedName("interestRate")
    val interestRate: Double,

    @SerializedName("latitude")
    val latitude: String? = null,

    @SerializedName("longitude")
    val longitude: String? = null
)