package com.example.ehefin_mobile.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.ehefin_mobile.feature.loan.data.source.local.BranchDao
import com.example.ehefin_mobile.feature.loan.data.source.local.BranchEntity
import com.example.ehefin_mobile.feature.loan.data.source.local.LoanDao
import com.example.ehefin_mobile.feature.loan.data.source.local.LoanEntity
import com.example.ehefin_mobile.feature.loan.data.source.local.LoanHistoryDao
import com.example.ehefin_mobile.feature.loan.data.source.local.LoanHistoryEntity
import com.example.ehefin_mobile.feature.plafond.data.source.local.PlafondDao
import com.example.ehefin_mobile.feature.plafond.data.source.local.PlafondEntity
import com.example.ehefin_mobile.feature.plafond.data.source.local.ProductDao
import com.example.ehefin_mobile.feature.plafond.data.source.local.ProductEntity
import com.example.ehefin_mobile.feature.profile.data.source.local.ProfileDao
import com.example.ehefin_mobile.feature.profile.data.source.local.ProfileEntity
import com.example.ehefin_mobile.core.database.entity.PendingRequestEntity
import com.example.ehefin_mobile.core.database.dao.PendingRequestDao

@Database(
    entities = [
        ProfileEntity::class,
        ProductEntity::class,
        PlafondEntity::class,
        LoanEntity::class,
        LoanHistoryEntity::class,
        BranchEntity::class,
        PendingRequestEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class EheFinDatabase : RoomDatabase() {
    
    // Profile
    abstract fun profileDao(): ProfileDao
    
    // Plafond & Products
    abstract fun productDao(): ProductDao
    abstract fun plafondDao(): PlafondDao
    
    // Loans
    abstract fun loanDao(): LoanDao
    abstract fun loanHistoryDao(): LoanHistoryDao
    abstract fun branchDao(): BranchDao
    
    // Offline Sync
    abstract fun pendingRequestDao(): PendingRequestDao
}
