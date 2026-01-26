package com.example.ehefin_mobile.feature.loan.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for Loan History operations
 */
@Dao
interface LoanHistoryDao {
    
    @Query("SELECT * FROM loan_history WHERE loanId = :loanId ORDER BY createdAt ASC")
    fun getHistoryByLoanId(loanId: Long): Flow<List<LoanHistoryEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: List<LoanHistoryEntity>)
    
    @Query("DELETE FROM loan_history WHERE loanId = :loanId")
    suspend fun deleteHistoryByLoanId(loanId: Long)
    
    @Query("DELETE FROM loan_history")
    suspend fun deleteAllHistory()
    
    @Transaction
    suspend fun replaceHistoryForLoan(loanId: Long, history: List<LoanHistoryEntity>) {
        deleteHistoryByLoanId(loanId)
        insertHistory(history)
    }
}
