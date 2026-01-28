package com.example.ehefin_mobile.feature.plafond.domain.usecase

import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.feature.plafond.domain.model.Product
import com.example.ehefin_mobile.feature.plafond.domain.repository.ProductRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/** Use case to get all available products */
class GetProductsUseCase @Inject constructor(private val productRepository: ProductRepository) {
    operator fun invoke(): Flow<Resource<List<Product>>> {
        return productRepository.getProducts()
    }
}