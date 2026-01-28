package com.example.ehefin_mobile.feature.profile.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for Profile operations
 */
@Dao
interface ProfileDao {
    
    @Query("SELECT * FROM profiles WHERE userId = :userId")
    fun getProfileByUserId(userId: Long): Flow<ProfileEntity?>
    
    @Query("SELECT * FROM profiles WHERE userId = :userId")
    suspend fun getProfileByUserIdSync(userId: Long): ProfileEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity)
    
    @Query("DELETE FROM profiles WHERE userId = :userId")
    suspend fun deleteProfile(userId: Long)
    
    @Query("DELETE FROM profiles")
    suspend fun deleteAllProfiles()
}