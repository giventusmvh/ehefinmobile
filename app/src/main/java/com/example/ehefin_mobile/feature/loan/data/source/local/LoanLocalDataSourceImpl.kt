package com.example.ehefin_mobile.feature.loan.data.source.local

import com.example.ehefin_mobile.core.common.DataResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementation of LoanLocalDataSource.
 * Wraps Room DAO calls with proper error handling and returns DataResult.
 */
class LoanLocalDataSourceImpl @Inject constructor(
    private val loanDao: LoanDao,
    private val branchDao: BranchDao,
    private val loanHistoryDao: LoanHistoryDao
) : LoanLocalDataSource {

    override fun getAllLoansFlow(): Flow<List<LoanEntity>> {
        return loanDao.getAllLoans()
    }

    override fun getLoanByIdFlow(loanId: Long): Flow<LoanEntity?> {
        return loanDao.getLoanById(loanId)
    }

    override suspend fun getLoanById(loanId: Long): DataResult<LoanEntity?> {
        return try {
            val loan = loanDao.getLoanByIdSync(loanId)
            DataResult.Success(loan)
        } catch (e: Exception) {
            DataResult.Error(
                message = e.localizedMessage ?: "Gagal mengambil data pinjaman",
                throwable = e
            )
        }
    }

    override suspend fun saveLoan(loan: LoanEntity): DataResult<Unit> {
        return try {
            loanDao.insertLoan(loan)
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(
                message = e.localizedMessage ?: "Gagal menyimpan data pinjaman",
                throwable = e
            )
        }
    }

    override suspend fun saveLoans(loans: List<LoanEntity>): DataResult<Unit> {
        return try {
            loanDao.insertLoans(loans)
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(
                message = e.localizedMessage ?: "Gagal menyimpan data pinjaman",
                throwable = e
            )
        }
    }

    override suspend fun replaceAllLoans(loans: List<LoanEntity>): DataResult<Unit> {
        return try {
            loanDao.deleteAllAndInsert(loans)
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(
                message = e.localizedMessage ?: "Gagal mengganti data pinjaman",
                throwable = e
            )
        }
    }

    override suspend fun deleteAllLoans(): DataResult<Unit> {
        return try {
            loanDao.deleteAllLoans()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(
                message = e.localizedMessage ?: "Gagal menghapus data pinjaman",
                throwable = e
            )
        }
    }

    override fun getAllBranchesFlow(): Flow<List<BranchEntity>> {
        return branchDao.getAllBranches()
    }

    override suspend fun saveBranches(branches: List<BranchEntity>): DataResult<Unit> {
        return try {
            branchDao.deleteAllAndInsert(branches)
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(
                message = e.localizedMessage ?: "Gagal menyimpan data cabang",
                throwable = e
            )
        }
    }

    override fun getLoanHistoryFlow(loanId: Long): Flow<List<LoanHistoryEntity>> {
        return loanHistoryDao.getHistoryByLoanId(loanId)
    }

    override suspend fun saveLoanHistory(history: List<LoanHistoryEntity>): DataResult<Unit> {
        return try {
            if (history.isNotEmpty()) {
                loanHistoryDao.replaceHistoryForLoan(history.first().loanId, history)
            }
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(
                message = e.localizedMessage ?: "Gagal menyimpan riwayat pinjaman",
                throwable = e
            )
        }
    }
}
