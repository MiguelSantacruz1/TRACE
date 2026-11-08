package com.ejemplo.trace.di

import com.ejemplo.trace.data.remote.ai.TraceBackendAiProvider
import com.ejemplo.trace.domain.ai.AiProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiModule {

    @Binds
    @Singleton
    abstract fun bindAiProvider(
        impl: TraceBackendAiProvider
    ): AiProvider
}
