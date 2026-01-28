package com.example.ehefin_mobile.feature.loan.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for Branch operations
 */
@Dao
interface BranchDao {
    
    @Query("SELECT * FROM branches ORDER BY name ASC")
    fun getAllBranches(): Flow<List<BranchEntity>>
    
    @Query("SELECT * FROM branches WHERE id = :branchId")
    suspend fun getBranchById(branchId: Long): BranchEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranches(branches: List<BranchEntity>)
    
    @Query("DELETE FROM branches")
    suspend fun deleteAllBranches()
    
    @Transaction
    suspend fun deleteAllAndInsert(branches: List<BranchEntity>) {
        deleteAllBranches()
        insertBranches(branches)
    }
}