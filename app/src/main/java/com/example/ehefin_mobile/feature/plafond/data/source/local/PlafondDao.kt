package com.example.ehefin_mobile.feature.plafond.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for Plafond operations
 */
@Dao
interface PlafondDao {
    
    @Query("SELECT * FROM plafonds WHERE userId = :userId AND isActive = 1 LIMIT 1")
    fun getActivePlafondByUserId(userId: Long): Flow<PlafondEntity?>
    
    @Query("SELECT * FROM plafonds WHERE userId = :userId AND isActive = 1 LIMIT 1")
    suspend fun getActivePlafondByUserIdSync(userId: Long): PlafondEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlafond(plafond: PlafondEntity)
    
    @Query("DELETE FROM plafonds WHERE userId = :userId")
    suspend fun deletePlafondByUserId(userId: Long)
    
    @Query("DELETE FROM plafonds")
    suspend fun deleteAllPlafonds()
}
