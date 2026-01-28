package com.example.ehefin_mobile.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ehefin_mobile.core.database.entity.PendingRequestEntity

@Dao
interface PendingRequestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(request: PendingRequestEntity): Long

    @Query("SELECT * FROM pending_requests ORDER BY createdAt ASC")
    suspend fun getAllRequests(): List<PendingRequestEntity>

    @Query("SELECT * FROM pending_requests WHERE id = :id")
    suspend fun getRequestById(id: Long): PendingRequestEntity?

    @Delete
    suspend fun delete(request: PendingRequestEntity)

    @Query("DELETE FROM pending_requests WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Query("DELETE FROM pending_requests")
    suspend fun clearAll()
}