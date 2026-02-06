package com.example.ehefin_mobile.feature.profile.data.source.local

import com.example.ehefin_mobile.core.common.DataResult

/**
 * Interface for Profile local data source operations.
 * Abstracts Room DAO calls for testability and separation of concerns.
 */
interface ProfileLocalDataSource {
    
    /**
     * Get profile from local database by user ID.
     */
    suspend fun getProfile(userId: Long): DataResult<ProfileEntity?>
    
    /**
     * Save profile to local database.
     */
    suspend fun saveProfile(profile: ProfileEntity): DataResult<Unit>
    
    /**
     * Delete profile from local database.
     */
    suspend fun deleteProfile(userId: Long): DataResult<Unit>
    
    /**
     * Clear all profiles from local database.
     */
    suspend fun clearAllProfiles(): DataResult<Unit>
}