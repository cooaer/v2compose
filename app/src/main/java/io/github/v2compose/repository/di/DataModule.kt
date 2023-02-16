package io.github.v2compose.repository.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.v2compose.repository.*
import io.github.v2compose.repository.def.*

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun provideAppRepository(defaultAppRepository: DefaultAppRepository): AppRepository

    @Binds
    abstract fun provideNewsRepository(defaultNewsRepository: DefaultNewsRepository): NewsRepository

    @Binds
    abstract fun provideNodeRepository(defaultNodeRepository: DefaultNodeRepository): NodeRepository

    @Binds
    abstract fun provideTopicRepository(defaultTopicRepository: DefaultTopicRepository): TopicRepository

    @Binds
    abstract fun provideUserRepository(defaultUserRepository: DefaultUserRepository): UserRepository

    @Binds
    abstract fun provideAccountRepository(defaultAccountRepository: DefaultAccountRepository): AccountRepository

}