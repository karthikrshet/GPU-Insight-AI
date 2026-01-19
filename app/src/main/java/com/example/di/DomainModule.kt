package com.example.di

import com.example.data.GpuInsightRepository
import com.example.domain.*
import com.example.network.GeminiApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {
    @Provides @Singleton
    fun provideGetMetrics(repo: GpuInsightRepository) = GetGpuMetricsUseCase(repo)

    @Provides @Singleton
    fun provideAnalyzeError(api: GeminiApiService) = AnalyzeGpuErrorUseCase(api)

    @Provides @Singleton
    fun provideChaosEngine(repo: GpuInsightRepository) = ChaosEngineUseCase(repo)

    @Provides @Singleton
    fun provideReportGenerator() = ReportGenerator()
}
