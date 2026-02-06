package com.example.ehefin_mobile.feature.loan.di

import com.example.ehefin_mobile.feature.loan.data.repository.BranchRepositoryImpl
import com.example.ehefin_mobile.feature.loan.data.repository.LoanRepositoryImpl
import com.example.ehefin_mobile.feature.loan.data.source.local.LoanLocalDataSource
import com.example.ehefin_mobile.feature.loan.data.source.local.LoanLocalDataSourceImpl
import com.example.ehefin_mobile.feature.loan.data.source.remote.LoanApi
import com.example.ehefin_mobile.feature.loan.data.source.remote.LoanRemoteDataSource
import com.example.ehefin_mobile.feature.loan.data.source.remote.LoanRemoteDataSourceImpl
import com.example.ehefin_mobile.feature.loan.domain.repository.BranchRepository
import com.example.ehefin_mobile.feature.loan.domain.repository.LoanRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LoanModule {

    @Binds
    @Singleton
    abstract fun bindLoanRepository(
        loanRepositoryImpl: LoanRepositoryImpl
    ): LoanRepository

    @Binds
    @Singleton
    abstract fun bindBranchRepository(
        branchRepositoryImpl: BranchRepositoryImpl
    ): BranchRepository

    @Binds
    @Singleton
    abstract fun bindLoanRemoteDataSource(
        loanRemoteDataSourceImpl: LoanRemoteDataSourceImpl
    ): LoanRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindLoanLocalDataSource(
        loanLocalDataSourceImpl: LoanLocalDataSourceImpl
    ): LoanLocalDataSource

    companion object {
        @Provides
        @Singleton
        fun provideLoanApi(retrofit: Retrofit): LoanApi {
            return retrofit.create(LoanApi::class.java)
        }
    }
}