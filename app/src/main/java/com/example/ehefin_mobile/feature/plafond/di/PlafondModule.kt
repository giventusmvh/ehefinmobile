package com.example.ehefin_mobile.feature.plafond.di

import com.example.ehefin_mobile.feature.plafond.data.repository.PlafondRepositoryImpl
import com.example.ehefin_mobile.feature.plafond.data.repository.ProductRepositoryImpl
import com.example.ehefin_mobile.feature.plafond.data.source.remote.PlafondApi
import com.example.ehefin_mobile.feature.plafond.domain.repository.PlafondRepository
import com.example.ehefin_mobile.feature.plafond.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

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

    companion object {
        @Provides
        @Singleton
        fun providePlafondApi(retrofit: Retrofit): PlafondApi {
            return retrofit.create(PlafondApi::class.java)
        }
    }
}