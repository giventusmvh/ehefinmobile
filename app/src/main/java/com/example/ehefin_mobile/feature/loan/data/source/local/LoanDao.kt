package com.example.ehefin_mobile.feature.loan.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for Loan operations
 */
@Dao
interface LoanDao {
    
    @Query("SELECT * FROM loans ORDER BY createdAt DESC")
    fun getAllLoans(): Flow<List<LoanEntity>>
    
    @Query("SELECT * FROM loans WHERE id = :loanId")
    fun getLoanById(loanId: Long): Flow<LoanEntity?>
    
    @Query("SELECT * FROM loans WHERE id = :loanId")
    suspend fun getLoanByIdSync(loanId: Long): LoanEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: LoanEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoans(loans: List<LoanEntity>)
    
    @Query("DELETE FROM loans")
    suspend fun deleteAllLoans()
    
    @Query("DELETE FROM loans WHERE id = :loanId")
    suspend fun deleteLoan(loanId: Long)
    
    @Transaction
    suspend fun deleteAllAndInsert(loans: List<LoanEntity>) {
        deleteAllLoans()
        insertLoans(loans)
    }
}