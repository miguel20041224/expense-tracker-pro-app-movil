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
import kotlinx.coroutines.flow.first
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
    suspend fun ensureCatalog(userId: Long) {
        val now = System.currentTimeMillis()
        if (catalogDao.getDefaultAccountId(userId) == null) {
            catalogDao.insertAccounts(
                listOf(
                    AccountEntity(
                        userId = userId,
                        name = "Cuenta principal",
                        currencyCode = "USD",
                        type = "CHECKING",
                        createdAt = now,
                    ),
                ),
            )
        }
        if (catalogDao.getCategoryCount(userId) == 0) {
            catalogDao.insertCategories(defaultCategories(userId))
        }
    }

    suspend fun seedIfEmpty(userId: Long) {
        ensureCatalog(userId)
        val accountId = catalogDao.getDefaultAccountId(userId) ?: return
        val expenseCats = catalogDao.observeCategoriesByType(userId, TransactionType.EXPENSE.storageValue).first()
        val foodId = expenseCats.find { it.name == "Comida" }?.id ?: expenseCats.firstOrNull()?.id ?: return
        val transportId = expenseCats.find { it.name == "Transporte" }?.id ?: foodId
        val homeId = expenseCats.find { it.name == "Hogar" }?.id ?: foodId
        val incomeCats = catalogDao.observeCategoriesByType(userId, TransactionType.INCOME.storageValue).first()
        val salaryId = incomeCats.find { it.name == "Salario" }?.id ?: incomeCats.firstOrNull()?.id ?: return
        val freelanceId = incomeCats.find { it.name == "Freelance" }?.id ?: salaryId

        val now = System.currentTimeMillis()
        val month = MonthRange.current()
        val base = ZonedDateTime.now().withDayOfMonth(5)
        transactionDao.insertAll(
            listOf(
                tx(userId, accountId, salaryId, TransactionType.INCOME, 3_200_00, "Nómina", base.minusDays(2)),
                tx(userId, accountId, foodId, TransactionType.EXPENSE, 85_00, "Supermercado", base),
                tx(userId, accountId, transportId, TransactionType.EXPENSE, 42_00, "Metro", base.plusDays(1)),
                tx(userId, accountId, foodId, TransactionType.EXPENSE, 120_00, "Restaurante", base.plusDays(3)),
                tx(userId, accountId, homeId, TransactionType.EXPENSE, 950_00, "Alquiler", base.plusDays(4)),
                tx(userId, accountId, freelanceId, TransactionType.INCOME, 450_00, "Proyecto freelance", base.plusDays(6)),
            ),
        )

        financeCatalogDao.insertBudgets(
            listOf(
                BudgetEntity(userId = userId, categoryId = foodId, amountMinor = 400_00, period = "MONTHLY", startAt = month.startEpochMilli, endAt = month.endEpochMilli),
                BudgetEntity(userId = userId, categoryId = transportId, amountMinor = 150_00, period = "MONTHLY", startAt = month.startEpochMilli, endAt = month.endEpochMilli),
            ),
        )
        financeCatalogDao.insertGoals(
            listOf(
                GoalEntity(userId = userId, name = "Fondo de emergencia", targetMinor = 5_000_00, savedMinor = 1_250_00, deadlineAt = ZonedDateTime.now().plusMonths(8).toInstant().toEpochMilli(), type = "SAVINGS", createdAt = now),
            ),
        )
        financeCatalogDao.insertDebts(
            listOf(
                DebtEntity(userId = userId, name = "Tarjeta de crédito", principalMinor = 2_400_00, remainingMinor = 1_200_00, interestRate = 18.5, minimumPaymentMinor = 150_00, dueAt = ZonedDateTime.now().plusDays(12).toInstant().toEpochMilli(), createdAt = now),
            ),
        )
        financeCatalogDao.insertProjections(
            listOf(
                ProjectionEntity(userId = userId, type = "SAVINGS", title = "Ahorro proyectado 6 meses", metadataJson = """{"amountMinor":180000}""", projectedAt = ZonedDateTime.now().plusMonths(6).toInstant().toEpochMilli(), createdAt = now),
            ),
        )
        insightDao.insertAlerts(
            listOf(
                AlertEntity(userId = userId, type = "BUDGET", title = "Presupuesto de comida", message = "Llevas el 72% del presupuesto mensual en Comida.", createdAt = now),
            ),
        )
    }

    private fun defaultCategories(userId: Long) = listOf(
        CategoryEntity(userId = userId, name = "Salario", type = TransactionType.INCOME.storageValue, icon = "work", colorHex = "#22C55E"),
        CategoryEntity(userId = userId, name = "Freelance", type = TransactionType.INCOME.storageValue, icon = "payments", colorHex = "#16A34A"),
        CategoryEntity(userId = userId, name = "Comida", type = TransactionType.EXPENSE.storageValue, icon = "restaurant", colorHex = "#F97316"),
        CategoryEntity(userId = userId, name = "Transporte", type = TransactionType.EXPENSE.storageValue, icon = "directions_car", colorHex = "#3B82F6"),
        CategoryEntity(userId = userId, name = "Hogar", type = TransactionType.EXPENSE.storageValue, icon = "home", colorHex = "#8B5CF6"),
    )

    private fun tx(
        userId: Long,
        accountId: Long,
        categoryId: Long,
        type: TransactionType,
        amountMinor: Long,
        note: String,
        at: ZonedDateTime,
    ): TransactionEntity {
        val millis = at.toInstant().toEpochMilli()
        return TransactionEntity(
            userId = userId,
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
