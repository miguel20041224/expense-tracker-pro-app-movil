package com.finpulse.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.finpulse.data.local.db.dao.CatalogDao
import com.finpulse.data.local.db.dao.DashboardDao
import com.finpulse.data.local.db.dao.DebtDao
import com.finpulse.data.local.db.dao.FinanceCatalogDao
import com.finpulse.data.local.db.dao.InsightDao
import com.finpulse.data.local.db.dao.TransactionDao
import com.finpulse.data.local.db.dao.UserDao
import com.finpulse.data.local.db.entity.AccountEntity
import com.finpulse.data.local.db.entity.AlertEntity
import com.finpulse.data.local.db.entity.BudgetEntity
import com.finpulse.data.local.db.entity.CategoryEntity
import com.finpulse.data.local.db.entity.DebtEntity
import com.finpulse.data.local.db.entity.FinancialInsightEntity
import com.finpulse.data.local.db.entity.GoalEntity
import com.finpulse.data.local.db.entity.ProjectionEntity
import com.finpulse.data.local.db.entity.TransactionEntity
import com.finpulse.data.local.db.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        AccountEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        BudgetEntity::class,
        GoalEntity::class,
        DebtEntity::class,
        ProjectionEntity::class,
        FinancialInsightEntity::class,
        AlertEntity::class,
    ],
    version = 4,
    exportSchema = false,
)
abstract class FinPulseDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun catalogDao(): CatalogDao
    abstract fun transactionDao(): TransactionDao
    abstract fun dashboardDao(): DashboardDao
    abstract fun insightDao(): InsightDao
    abstract fun financeCatalogDao(): FinanceCatalogDao
    abstract fun debtDao(): DebtDao
}
