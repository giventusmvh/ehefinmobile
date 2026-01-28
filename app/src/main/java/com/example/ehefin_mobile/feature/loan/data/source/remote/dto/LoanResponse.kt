package com.example.ehefin_mobile.feature.loan.data.source.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Response DTO for loan detail with customer snapshot
 */
data class LoanResponseDto(
    @SerializedName("id")
    val id: Long,
    
    @SerializedName("customerId")
    val customerId: Long?,
    
    @SerializedName("customerName")
    val customerName: String,
    
    @SerializedName("customerEmail")
    val customerEmail: String?,
    
    @SerializedName("customerNik")
    val customerNik: String?,
    
    @SerializedName("customerPhone")
    val customerPhone: String?,
    
    @SerializedName("customerAddress")
    val customerAddress: String?,
    
    @SerializedName("customerBirthdate")
    val customerBirthdate: String?,
    
    @SerializedName("customerKtpPath")
    val customerKtpPath: String?,
    
    @SerializedName("customerKkPath")
    val customerKkPath: String?,
    
    @SerializedName("customerNpwpPath")
    val customerNpwpPath: String?,
    
    @SerializedName("customerBankName")
    val customerBankName: String?,
    
    @SerializedName("customerAccountNumber")
    val customerAccountNumber: String?,
    
    @SerializedName("customerAccountHolderName")
    val customerAccountHolderName: String?,
    
    @SerializedName("product")
    val product: ProductDto?,
    
    @SerializedName("productId")
    val productId: Long?,
    
    @SerializedName("productName")
    val productName: String?,
    
    @SerializedName("branch")
    val branch: BranchDto?,
    
    @SerializedName("branchId")
    val branchId: Long?,
    
    @SerializedName("branchName")
    val branchName: String?,
    
    @SerializedName("requestedAmount")
    val requestedAmount: Double?,
    
    @SerializedName("amount")
    val amount: Double?,
    
    @SerializedName("requestedTenor")
    val requestedTenor: Int?,
    
    @SerializedName("tenor")
    val tenor: Int?,
    
    @SerializedName("requestedRate")
    val requestedRate: Double?,
    
    @SerializedName("interestRate")
    val interestRate: Double?,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("createdAt")
    val createdAt: String,
    
    @SerializedName("updatedAt")
    val updatedAt: String?
)

data class ProductDto(
    @SerializedName("id")
    val id: Long,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("amount")
    val amount: Double,
    
    @SerializedName("tenor")
    val tenor: Int,
    
    @SerializedName("interestRate")
    val interestRate: Double
)

data class BranchDto(
    @SerializedName("id")
    val id: Long,
    
    @SerializedName("code")
    val code: String,
    
    @SerializedName("location")
    val location: String?,
    
    @SerializedName("name")
    val name: String?
)

/**
 * Response DTO for loan history entry
 */
data class LoanHistoryDto(
    @SerializedName("id")
    val id: Long,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("note")
    val note: String?,
    
    @SerializedName("approvedByName")
    val approvedBy: String?,
    
    @SerializedName("approvedByRole")
    val approvedByRole: String?,
    
    @SerializedName("approvedByBranch")
    val approvedByBranchName: String?,
    
    @SerializedName("createdAt")
    val createdAt: String
)
