package com.example.ehefin_mobile.feature.loan.data.mapper

import com.example.ehefin_mobile.feature.loan.data.source.local.BranchEntity
import com.example.ehefin_mobile.feature.loan.data.source.local.LoanEntity
import com.example.ehefin_mobile.feature.loan.data.source.local.LoanHistoryEntity
import com.example.ehefin_mobile.feature.loan.data.source.remote.dto.BranchDto
import com.example.ehefin_mobile.feature.loan.data.source.remote.dto.LoanHistoryDto
import com.example.ehefin_mobile.feature.loan.data.source.remote.dto.LoanResponseDto
import com.example.ehefin_mobile.feature.loan.domain.model.Branch
import com.example.ehefin_mobile.feature.loan.domain.model.LoanApplication
import com.example.ehefin_mobile.feature.loan.domain.model.LoanHistory
import com.example.ehefin_mobile.feature.loan.domain.model.LoanItem
import com.example.ehefin_mobile.feature.loan.domain.model.LoanStatus

/**
 * Mapper extension functions for Loan
 */

// DTO -> Entity
fun LoanResponseDto.toEntity(): LoanEntity {
    return LoanEntity(
        id = id,
        customerId = customerId ?: 0,
        customerName = customerName,
        customerEmail = customerEmail ?: "",
        customerNik = customerNik,
        customerPhone = customerPhone,
        customerAddress = customerAddress,
        customerBirthdate = customerBirthdate,
        customerKtpPath = customerKtpPath,
        customerKkPath = customerKkPath,
        customerNpwpPath = customerNpwpPath,
        customerBankName = customerBankName,
        customerAccountNumber = customerAccountNumber,
        customerAccountHolderName = customerAccountHolderName,
        productId = product?.id ?: productId ?: 0,
        productName = product?.name ?: productName ?: "",
        branchId = branch?.id ?: branchId ?: 0,
        branchName = branch?.location ?: branch?.name ?: branchName ?: "",
        requestedAmount = requestedAmount ?: amount ?: 0.0,
        requestedTenor = requestedTenor ?: tenor ?: 0,
        requestedRate = requestedRate ?: interestRate ?: 0.0,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

// Entity -> Domain
fun LoanEntity.toDomain(): LoanApplication {
    return LoanApplication(
        id = id,
        customerId = customerId,
        customerName = customerName,
        customerEmail = customerEmail,
        customerNik = customerNik,
        customerPhone = customerPhone,
        customerAddress = customerAddress,
        customerBirthdate = customerBirthdate,
        customerKtpPath = customerKtpPath,
        customerKkPath = customerKkPath,
        customerNpwpPath = customerNpwpPath,
        customerBankName = customerBankName,
        customerAccountNumber = customerAccountNumber,
        customerAccountHolderName = customerAccountHolderName,
        productId = productId,
        productName = productName,
        branchId = branchId,
        branchName = branchName,
        requestedAmount = requestedAmount,
        requestedTenor = requestedTenor,
        requestedRate = requestedRate,
        status = LoanStatus.fromString(status),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

// Entity -> LoanItem (for list display)
fun LoanEntity.toLoanItem(): LoanItem {
    return LoanItem(
        id = id,
        customerName = customerName,
        productName = productName,
        branchName = branchName,
        amount = requestedAmount,
        tenor = requestedTenor,
        interestRate = requestedRate,
        status = LoanStatus.fromString(status),
        createdAt = createdAt
    )
}

// DTO -> Domain (direct)
fun LoanResponseDto.toDomain(): LoanApplication {
    return toEntity().toDomain()
}

/**
 * Mapper extension functions for LoanHistory
 */

// DTO -> Entity
fun LoanHistoryDto.toEntity(loanId: Long): LoanHistoryEntity {
    return LoanHistoryEntity(
        id = id,
        loanId = loanId,
        status = status,
        note = note,
        approvedBy = approvedBy,
        approvedByRole = approvedByRole,
        approvedByBranchName = approvedByBranchName,
        createdAt = createdAt
    )
}

// Entity -> Domain
fun LoanHistoryEntity.toDomain(): LoanHistory {
    return LoanHistory(
        id = id,
        loanId = loanId,
        status = LoanStatus.fromString(status),
        note = note,
        approvedBy = approvedBy,
        approvedByRole = approvedByRole,
        approvedByBranchName = approvedByBranchName,
        createdAt = createdAt
    )
}

/**
 * Mapper extension functions for Branch
 */

// DTO -> Entity
fun BranchDto.toEntity(): BranchEntity {
    return BranchEntity(
        id = id,
        code = code,
        name = location ?: name ?: ""
    )
}

// Entity -> Domain
fun BranchEntity.toDomain(): Branch {
    return Branch(
        id = id,
        code = code,
        name = name
    )
}