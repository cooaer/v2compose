package io.github.v2compose.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.v2compose.data.NewsRepository
import io.github.v2compose.data.NodeRepository
import io.github.v2compose.data.def.DefaultNewsRepository
import io.github.v2compose.data.def.DefaultNodeRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    abstract fun provideNewsRepository(defaultNewsRepository: DefaultNewsRepository): NewsRepository

    @Binds
    abstract fun provideNodeRepository(defaultNodeRepository: DefaultNodeRepository): NodeRepository
}