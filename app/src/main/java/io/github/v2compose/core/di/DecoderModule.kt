package io.github.v2compose.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.v2compose.core.StringDecoder
import io.github.v2compose.core.UriDecoder

@Module
@InstallIn(SingletonComponent::class)
abstract class DecoderModule {
    @Binds
    abstract fun provideStringDecoder(uriDecoder: UriDecoder): StringDecoder
}