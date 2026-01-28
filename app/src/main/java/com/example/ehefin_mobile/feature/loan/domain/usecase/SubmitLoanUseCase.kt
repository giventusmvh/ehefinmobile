package com.example.ehefin_mobile.feature.loan.domain.usecase

import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.feature.loan.domain.model.LoanApplication
import com.example.ehefin_mobile.feature.loan.domain.model.SubmitLoanRequest
import com.example.ehefin_mobile.feature.loan.domain.repository.LoanRepository
import javax.inject.Inject

/**
 * Use case for submitting new loan application (SRP)
 */
class SubmitLoanUseCase @Inject constructor(
    private val loanRepository: LoanRepository
) {
    suspend operator fun invoke(
        branchId: Long,
        amount: Double,
        tenor: Int,
        interestRate: Double
    ): Resource<LoanApplication> {
        // Validation
        if (branchId <= 0) {
            return Resource.Error("Pilih cabang yang valid")
        }
        if (amount <= 0) {
            return Resource.Error("Jumlah pinjaman harus lebih dari 0")
        }
        if (tenor <= 0 || tenor > 48) {
            return Resource.Error("Tenor harus antara 1-48 bulan")
        }
        if (interestRate <= 0) {
            return Resource.Error("Suku bunga tidak valid")
        }
        
        val request = SubmitLoanRequest(
            branchId = branchId,
            amount = amount,
            tenor = tenor,
            interestRate = interestRate
        )
        
        return loanRepository.submitLoan(request)
    }
}