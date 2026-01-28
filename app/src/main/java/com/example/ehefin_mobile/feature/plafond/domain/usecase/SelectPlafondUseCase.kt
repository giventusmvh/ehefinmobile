package com.example.ehefin_mobile.feature.plafond.domain.usecase

import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.feature.plafond.domain.model.UserPlafond
import com.example.ehefin_mobile.feature.plafond.domain.repository.PlafondRepository
import javax.inject.Inject

/** Use case to select/apply for a plafond product */
class SelectPlafondUseCase @Inject constructor(private val plafondRepository: PlafondRepository) {
    suspend operator fun invoke(productId: Long): Resource<UserPlafond> {
        if (productId <= 0) {
            return Resource.Error("Produk tidak valid")
        }
        return plafondRepository.selectPlafond(productId)
    }
}