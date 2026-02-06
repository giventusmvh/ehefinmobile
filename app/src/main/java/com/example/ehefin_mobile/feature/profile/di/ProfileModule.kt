package com.example.ehefin_mobile.feature.profile.di

import com.example.ehefin_mobile.feature.profile.data.repository.ProfileRepositoryImpl
import com.example.ehefin_mobile.feature.profile.data.source.local.ProfileLocalDataSource
import com.example.ehefin_mobile.feature.profile.data.source.local.ProfileLocalDataSourceImpl
import com.example.ehefin_mobile.feature.profile.data.source.remote.ProfileApi
import com.example.ehefin_mobile.feature.profile.data.source.remote.ProfileRemoteDataSource
import com.example.ehefin_mobile.feature.profile.data.source.remote.ProfileRemoteDataSourceImpl
import com.example.ehefin_mobile.feature.profile.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileModule {

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        profileRepositoryImpl: ProfileRepositoryImpl
    ): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindProfileRemoteDataSource(
        profileRemoteDataSourceImpl: ProfileRemoteDataSourceImpl
    ): ProfileRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindProfileLocalDataSource(
        profileLocalDataSourceImpl: ProfileLocalDataSourceImpl
    ): ProfileLocalDataSource

    companion object {
        @Provides
        @Singleton
        fun provideProfileApi(retrofit: Retrofit): ProfileApi {
            return retrofit.create(ProfileApi::class.java)
        }
    }
}