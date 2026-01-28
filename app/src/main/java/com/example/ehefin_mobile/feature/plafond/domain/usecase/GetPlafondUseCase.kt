package com.example.ehefin_mobile.feature.plafond.domain.usecase

import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.feature.plafond.domain.model.UserPlafond
import com.example.ehefin_mobile.feature.plafond.domain.repository.PlafondRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/** Use case to get user's active plafond */
class GetPlafondUseCase @Inject constructor(private val plafondRepository: PlafondRepository) {
    operator fun invoke(): Flow<Resource<UserPlafond?>> {
        return plafondRepository.getActivePlafond()
    }
}