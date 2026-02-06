package com.example.ehefin_mobile.feature.plafond.data.source.local

import com.example.ehefin_mobile.core.common.DataResult
import kotlinx.coroutines.flow.Flow

/**
 * Interface for Plafond local data source operations.
 * Abstracts Room DAO calls for testability and separation of concerns.
 */
interface PlafondLocalDataSource {

    /**
     * Get active plafond as Flow for reactive updates.
     */
    fun getActivePlafondFlow(): Flow<PlafondEntity?>

    /**
     * Get active plafond synchronously.
     */
    suspend fun getActivePlafond(): DataResult<PlafondEntity?>

    /**
     * Save plafond to local storage.
     */
    suspend fun savePlafond(plafond: PlafondEntity): DataResult<Unit>

    /**
     * Clear plafond from local storage.
     */
    suspend fun clearPlafond(): DataResult<Unit>

    /**
     * Get all products as Flow.
     */
    fun getAllProductsFlow(): Flow<List<ProductEntity>>

    /**
     * Save products to local storage.
     */
    suspend fun saveProducts(products: List<ProductEntity>): DataResult<Unit>
}