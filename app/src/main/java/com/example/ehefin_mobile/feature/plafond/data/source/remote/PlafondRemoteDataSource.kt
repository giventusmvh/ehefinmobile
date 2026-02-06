package com.example.ehefin_mobile.feature.plafond.data.source.remote

import com.example.ehefin_mobile.core.common.DataResult
import com.example.ehefin_mobile.feature.plafond.data.source.remote.dto.PlafondResponseDto
import com.example.ehefin_mobile.feature.plafond.data.source.remote.dto.ProductResponseDto

/**
 * Interface for Plafond remote data source operations.
 * Abstracts API calls for testability and separation of concerns.
 */
interface PlafondRemoteDataSource {

    /**
     * Get all available products/plafond options.
     */
    suspend fun getProducts(): DataResult<List<ProductResponseDto>>

    /**
     * Get currently active plafond for the user.
     */
    suspend fun getActivePlafond(): DataResult<PlafondResponseDto>

    /**
     * Select/apply for a plafond.
     */
    suspend fun selectPlafond(productId: Long): DataResult<PlafondResponseDto>
}