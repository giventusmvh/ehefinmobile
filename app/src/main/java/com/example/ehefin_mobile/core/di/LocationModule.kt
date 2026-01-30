package com.example.ehefin_mobile.core.di

import com.example.ehefin_mobile.core.util.LocationHelper
import com.example.ehefin_mobile.core.util.LocationHelperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    @Binds
    @Singleton
    abstract fun bindLocationHelper(
        locationHelperImpl: LocationHelperImpl
    ): LocationHelper
}
