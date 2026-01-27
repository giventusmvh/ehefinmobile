package com.example.ehefin_mobile.core.di

import android.content.Context
import androidx.room.Room
import com.example.ehefin_mobile.core.common.Constants
import com.example.ehefin_mobile.core.database.EheFinDatabase
import com.example.ehefin_mobile.core.database.dao.PendingRequestDao
import com.example.ehefin_mobile.feature.loan.data.source.local.BranchDao
import com.example.ehefin_mobile.feature.loan.data.source.local.LoanDao
import com.example.ehefin_mobile.feature.loan.data.source.local.LoanHistoryDao
import com.example.ehefin_mobile.feature.plafond.data.source.local.PlafondDao
import com.example.ehefin_mobile.feature.plafond.data.source.local.ProductDao
import com.example.ehefin_mobile.feature.profile.data.source.local.ProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): EheFinDatabase {
        return Room.databaseBuilder(
            context,
            EheFinDatabase::class.java,
            Constants.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    @Singleton
    fun provideProfileDao(database: EheFinDatabase): ProfileDao {
        return database.profileDao()
    }
    
    @Provides
    @Singleton
    fun provideProductDao(database: EheFinDatabase): ProductDao {
        return database.productDao()
    }
    
    @Provides
    @Singleton
    fun providePlafondDao(database: EheFinDatabase): PlafondDao {
        return database.plafondDao()
    }
    
    @Provides
    @Singleton
    fun provideLoanDao(database: EheFinDatabase): LoanDao {
        return database.loanDao()
    }
    
    @Provides
    @Singleton
    fun provideLoanHistoryDao(database: EheFinDatabase): LoanHistoryDao {
        return database.loanHistoryDao()
    }
    
    @Provides
    @Singleton
    fun provideBranchDao(database: EheFinDatabase): BranchDao {
        return database.branchDao()
    }
    
    @Provides
    @Singleton
    fun providePendingRequestDao(database: EheFinDatabase): PendingRequestDao {
        return database.pendingRequestDao()
    }
}
