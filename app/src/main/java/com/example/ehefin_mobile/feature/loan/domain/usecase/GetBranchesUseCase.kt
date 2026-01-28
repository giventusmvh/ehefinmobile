package com.example.ehefin_mobile.feature.loan.domain.usecase

import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.feature.loan.domain.model.Branch
import com.example.ehefin_mobile.feature.loan.domain.repository.BranchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting branches (SRP)
 */
class GetBranchesUseCase @Inject constructor(
    private val branchRepository: BranchRepository
) {
    operator fun invoke(): Flow<Resource<List<Branch>>> {
        return branchRepository.getBranches()
    }
}