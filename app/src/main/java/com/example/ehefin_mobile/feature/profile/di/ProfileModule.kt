package com.example.ehefin_mobile.feature.profile.di

import com.example.ehefin_mobile.feature.profile.data.repository.ProfileRepositoryImpl
import com.example.ehefin_mobile.feature.profile.data.source.remote.ProfileApi
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
    
    companion object {
        @Provides
        @Singleton
        fun provideProfileApi(retrofit: Retrofit): ProfileApi {
            return retrofit.create(ProfileApi::class.java)
        }
    }
}