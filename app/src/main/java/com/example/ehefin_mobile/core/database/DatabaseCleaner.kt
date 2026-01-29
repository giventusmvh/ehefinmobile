package com.example.ehefin_mobile.core.database

import com.example.ehefin_mobile.core.database.dao.PendingRequestDao
import com.example.ehefin_mobile.feature.loan.data.source.local.BranchDao
import com.example.ehefin_mobile.feature.loan.data.source.local.LoanDao
import com.example.ehefin_mobile.feature.loan.data.source.local.LoanHistoryDao
import com.example.ehefin_mobile.feature.plafond.data.source.local.PlafondDao
import com.example.ehefin_mobile.feature.plafond.data.source.local.ProductDao
import com.example.ehefin_mobile.feature.profile.data.source.local.ProfileDao
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class to clear all Room database tables on logout.
 * This ensures no user data persists after logout.
 */
@Singleton
class DatabaseCleaner @Inject constructor(
    private val profileDao: ProfileDao,
    private val productDao: ProductDao,
    private val plafondDao: PlafondDao,
    private val loanDao: LoanDao,
    private val loanHistoryDao: LoanHistoryDao,
    private val branchDao: BranchDao,
    private val pendingRequestDao: PendingRequestDao
) {
    /**
     * Clears all user data from the database.
     * Should be called during logout to ensure data privacy.
     */
    suspend fun clearAllData() {
        // Clear profile data
        profileDao.deleteAllProfiles()
        
        // Clear plafond and product data
        plafondDao.deleteAllPlafonds()
        productDao.deleteAllProducts()
        
        // Clear loan data
        loanDao.deleteAllLoans()
        loanHistoryDao.deleteAllHistory()
        branchDao.deleteAllBranches()
        
        // Clear pending requests
        pendingRequestDao.clearAll()
    }
}