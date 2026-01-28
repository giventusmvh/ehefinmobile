package com.example.ehefin_mobile.feature.loan.domain.usecase

import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.feature.loan.domain.model.LoanApplication
import com.example.ehefin_mobile.feature.loan.domain.repository.LoanRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting loan detail (SRP)
 */
class GetLoanDetailUseCase @Inject constructor(
    private val loanRepository: LoanRepository
) {
    operator fun invoke(loanId: Long): Flow<Resource<LoanApplication>> {
        return loanRepository.getLoanById(loanId)
    }
}