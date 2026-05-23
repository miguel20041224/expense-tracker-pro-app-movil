package com.finpulse.data.local.seed

import com.finpulse.data.local.db.dao.CatalogDao
import com.finpulse.data.local.db.dao.FinanceCatalogDao
import com.finpulse.data.local.db.dao.InsightDao
import com.finpulse.data.local.db.dao.TransactionDao
import com.finpulse.data.local.db.entity.AccountEntity
import com.finpulse.data.local.db.entity.AlertEntity
import com.finpulse.data.local.db.entity.BudgetEntity
import com.finpulse.data.local.db.entity.CategoryEntity
import com.finpulse.data.local.db.entity.DebtEntity
import com.finpulse.data.local.db.entity.GoalEntity
import com.finpulse.data.local.db.entity.ProjectionEntity
import com.finpulse.data.local.db.entity.TransactionEntity
import com.finpulse.domain.model.TransactionType
import com.finpulse.util.MonthRange
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DemoDataSeeder @Inject constructor(
    private val catalogDao: CatalogDao,
    private val transactionDao: TransactionDao,
    private val insightDao: InsightDao,
    private val financeCatalogDao: FinanceCatalogDao,
) {
    suspend fun seedIfEmpty() {
        val now = System.currentTimeMillis()
        val month = MonthRange.current()

        catalogDao.insertAccounts(
            listOf(
                AccountEntity(
                    name = "Cuenta principal",
                    currencyCode = "USD",
                    type = "CHECKING",
                    createdAt = now,
                ),
            ),
        )
        catalogDao.insertCategories(
            listOf(
                CategoryEntity(name = "Salario", type = TransactionType.INCOME.storageValue, icon = "work", colorHex = "#22C55E"),
                CategoryEntity(name = "Freelance", type = TransactionType.INCOME.storageValue, icon = "payments", colorHex = "#16A34A"),
                CategoryEntity(name = "Comida", type = TransactionType.EXPENSE.storageValue, icon = "restaurant", colorHex = "#F97316"),
                CategoryEntity(name = "Transporte", type = TransactionType.EXPENSE.storageValue, icon = "directions_car", colorHex = "#3B82F6"),
                CategoryEntity(name = "Hogar", type = TransactionType.EXPENSE.storageValue, icon = "home", colorHex = "#8B5CF6"),
            ),
        )

        val base = ZonedDateTime.now().withDayOfMonth(5)
        transactionDao.insertAll(
            listOf(
                tx(1, 1, TransactionType.INCOME, 3_200_00, "Nómina", base.minusDays(2)),
                tx(1, 3, TransactionType.EXPENSE, 85_00, "Supermercado", base),
                tx(1, 4, TransactionType.EXPENSE, 42_00, "Metro", base.plusDays(1)),
                tx(1, 3, TransactionType.EXPENSE, 120_00, "Restaurante", base.plusDays(3)),
                tx(1, 5, TransactionType.EXPENSE, 950_00, "Alquiler", base.plusDays(4)),
                tx(1, 2, TransactionType.INCOME, 450_00, "Proyecto freelance", base.plusDays(6)),
            ),
        )

        financeCatalogDao.insertBudgets(
            listOf(
                BudgetEntity(
                    categoryId = 3,
                    amountMinor = 400_00,
                    period = "MONTHLY",
                    startAt = month.startEpochMilli,
                    endAt = month.endEpochMilli,
                ),
                BudgetEntity(
                    categoryId = 4,
                    amountMinor = 150_00,
                    period = "MONTHLY",
                    startAt = month.startEpochMilli,
                    endAt = month.endEpochMilli,
                ),
            ),
        )
        financeCatalogDao.insertGoals(
            listOf(
                GoalEntity(
                    name = "Fondo de emergencia",
                    targetMinor = 5_000_00,
                    savedMinor = 1_250_00,
                    deadlineAt = ZonedDateTime.now().plusMonths(8).toInstant().toEpochMilli(),
                    type = "SAVINGS",
                    createdAt = now,
                ),
            ),
        )
        financeCatalogDao.insertDebts(
            listOf(
                DebtEntity(
                    name = "Tarjeta de crédito",
                    principalMinor = 2_400_00,
                    remainingMinor = 1_200_00,
                    interestRate = 18.5,
                    dueAt = ZonedDateTime.now().plusDays(12).toInstant().toEpochMilli(),
                    createdAt = now,
                ),
            ),
        )
        financeCatalogDao.insertProjections(
            listOf(
                ProjectionEntity(
                    type = "SAVINGS",
                    title = "Ahorro proyectado 6 meses",
                    metadataJson = """{"amountMinor":180000}""",
                    projectedAt = ZonedDateTime.now().plusMonths(6).toInstant().toEpochMilli(),
                    createdAt = now,
                ),
            ),
        )

        insightDao.insertAlerts(
            listOf(
                AlertEntity(
                    type = "BUDGET",
                    title = "Presupuesto de comida",
                    message = "Llevas el 72% del presupuesto mensual en Comida.",
                    createdAt = now,
                ),
            ),
        )
    }

    private fun tx(
        accountId: Long,
        categoryId: Long,
        type: TransactionType,
        amountMinor: Long,
        note: String,
        at: ZonedDateTime,
    ): TransactionEntity {
        val millis = at.toInstant().toEpochMilli()
        return TransactionEntity(
            accountId = accountId,
            categoryId = categoryId,
            type = type.storageValue,
            amountMinor = amountMinor,
            note = note,
            occurredAt = millis,
            createdAt = millis,
        )
    }
}
