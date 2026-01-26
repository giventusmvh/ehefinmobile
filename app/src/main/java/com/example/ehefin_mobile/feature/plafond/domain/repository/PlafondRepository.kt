package com.example.ehefin_mobile.feature.plafond.domain.repository

import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.feature.plafond.domain.model.UserPlafond
import kotlinx.coroutines.flow.Flow

/** Repository interface for Plafond operations (DIP) */
interface PlafondRepository {

    /** Get user's active plafond (offline-first) */
    fun getActivePlafond(): Flow<Resource<UserPlafond?>>

    /** Select/Apply for a plafond product */
    suspend fun selectPlafond(productId: Long): Resource<UserPlafond>

    /** Refresh plafond from remote */
    suspend fun refreshPlafond(): Resource<Unit>
}
