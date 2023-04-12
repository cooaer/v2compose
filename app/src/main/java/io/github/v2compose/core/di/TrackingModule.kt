package io.github.v2compose.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.v2compose.core.analytics.IAnalytics
import io.github.v2compose.core.analytics.VendorAnalytics

@Module
@InstallIn(SingletonComponent::class)
abstract class TrackingModule {

    @Binds
    abstract fun provideAnalytics(analytics: VendorAnalytics): IAnalytics

}