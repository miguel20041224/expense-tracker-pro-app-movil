package com.finpulse.di

import android.content.Context
import androidx.room.Room
import dagger.hilt.android.qualifiers.ApplicationContext
import com.finpulse.data.local.db.FinPulseDatabase
import com.finpulse.data.local.db.dao.CatalogDao
import com.finpulse.data.local.db.dao.DashboardDao
import com.finpulse.data.local.db.dao.FinanceCatalogDao
import com.finpulse.data.local.db.dao.InsightDao
import com.finpulse.data.local.db.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FinPulseDatabase =
        Room.databaseBuilder(context, FinPulseDatabase::class.java, "finpulse.db")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides fun provideCatalogDao(db: FinPulseDatabase): CatalogDao = db.catalogDao()
    @Provides fun provideTransactionDao(db: FinPulseDatabase): TransactionDao = db.transactionDao()
    @Provides fun provideDashboardDao(db: FinPulseDatabase): DashboardDao = db.dashboardDao()
    @Provides fun provideInsightDao(db: FinPulseDatabase): InsightDao = db.insightDao()
    @Provides fun provideFinanceCatalogDao(db: FinPulseDatabase): FinanceCatalogDao = db.financeCatalogDao()
}
