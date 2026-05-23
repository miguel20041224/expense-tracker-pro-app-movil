package com.finpulse.di

import com.finpulse.data.repository.AlertRepositoryImpl
import com.finpulse.data.repository.AuthRepositoryImpl
import com.finpulse.data.repository.CategoryRepositoryImpl
import com.finpulse.data.repository.DashboardRepositoryImpl
import com.finpulse.data.repository.DebtRepositoryImpl
import com.finpulse.data.repository.FinanceCatalogRepositoryImpl
import com.finpulse.data.repository.FinancialIntelligenceRepositoryImpl
import com.finpulse.data.repository.TransactionRepositoryImpl
import com.finpulse.domain.repository.AlertRepository
import com.finpulse.domain.repository.AuthRepository
import com.finpulse.domain.repository.CategoryRepository
import com.finpulse.domain.repository.DashboardRepository
import com.finpulse.domain.repository.DebtRepository
import com.finpulse.domain.repository.FinanceCatalogRepository
import com.finpulse.domain.repository.FinancialIntelligenceRepository
import com.finpulse.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindDashboardRepository(impl: DashboardRepositoryImpl): DashboardRepository

    @Binds @Singleton
    abstract fun bindTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository

    @Binds @Singleton
    abstract fun bindDebtRepository(impl: DebtRepositoryImpl): DebtRepository

    @Binds @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds @Singleton
    abstract fun bindFinancialIntelligenceRepository(impl: FinancialIntelligenceRepositoryImpl): FinancialIntelligenceRepository

    @Binds @Singleton
    abstract fun bindAlertRepository(impl: AlertRepositoryImpl): AlertRepository

    @Binds @Singleton
    abstract fun bindFinanceCatalogRepository(impl: FinanceCatalogRepositoryImpl): FinanceCatalogRepository

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
