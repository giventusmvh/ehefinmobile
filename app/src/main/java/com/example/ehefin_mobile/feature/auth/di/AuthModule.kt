package com.example.ehefin_mobile.feature.auth.di

import com.example.ehefin_mobile.feature.auth.data.repository.AuthRepositoryImpl
import com.example.ehefin_mobile.feature.auth.data.source.remote.AuthApi
import com.example.ehefin_mobile.feature.auth.data.source.remote.AuthRemoteDataSource
import com.example.ehefin_mobile.feature.auth.data.source.remote.AuthRemoteDataSourceImpl
import com.example.ehefin_mobile.feature.auth.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindAuthRemoteDataSource(
        authRemoteDataSourceImpl: AuthRemoteDataSourceImpl
    ): AuthRemoteDataSource

    companion object {
        @Provides
        @Singleton
        fun provideAuthApi(retrofit: Retrofit): AuthApi {
            return retrofit.create(AuthApi::class.java)
        }
    }
}