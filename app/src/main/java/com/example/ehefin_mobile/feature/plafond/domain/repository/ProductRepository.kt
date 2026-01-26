package com.example.ehefin_mobile.feature.plafond.domain.repository

import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.feature.plafond.domain.model.Product
import kotlinx.coroutines.flow.Flow

/** Repository interface for Product operations (DIP) */
interface ProductRepository {

    /** Get all available products (offline-first) */
    fun getProducts(): Flow<Resource<List<Product>>>

    /** Refresh products from remote */
    suspend fun refreshProducts(): Resource<Unit>
}
