package com.example.ehefin_mobile.feature.plafond.di

import com.example.ehefin_mobile.feature.plafond.data.repository.PlafondRepositoryImpl
import com.example.ehefin_mobile.feature.plafond.data.repository.ProductRepositoryImpl
import com.example.ehefin_mobile.feature.plafond.data.source.local.PlafondLocalDataSource
import com.example.ehefin_mobile.feature.plafond.data.source.local.PlafondLocalDataSourceImpl
import com.example.ehefin_mobile.feature.plafond.data.source.remote.PlafondApi
import com.example.ehefin_mobile.feature.plafond.data.source.remote.PlafondRemoteDataSource
import com.example.ehefin_mobile.feature.plafond.data.source.remote.PlafondRemoteDataSourceImpl
import com.example.ehefin_mobile.feature.plafond.domain.repository.PlafondRepository
import com.example.ehefin_mobile.feature.plafond.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlafondModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindPlafondRepository(
        plafondRepositoryImpl: PlafondRepositoryImpl
    ): PlafondRepository

    @Binds
    @Singleton
    abstract fun bindPlafondRemoteDataSource(
        plafondRemoteDataSourceImpl: PlafondRemoteDataSourceImpl
    ): PlafondRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindPlafondLocalDataSource(
        plafondLocalDataSourceImpl: PlafondLocalDataSourceImpl
    ): PlafondLocalDataSource

    companion object {
        @Provides
        @Singleton
        fun providePlafondApi(retrofit: Retrofit): PlafondApi {
            return retrofit.create(PlafondApi::class.java)
        }
    }
}