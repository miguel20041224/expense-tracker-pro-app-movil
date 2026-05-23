package com.finpulse.data.mapper

import com.finpulse.data.local.db.entity.CategoryEntity
import com.finpulse.data.local.db.entity.DebtEntity
import com.finpulse.data.local.db.entity.GoalEntity
import com.finpulse.data.local.db.entity.TransactionEntity
import com.finpulse.data.local.db.model.TransactionWithCategoryRow
import com.finpulse.domain.model.Category
import com.finpulse.domain.model.Debt
import com.finpulse.domain.model.Goal
import com.finpulse.domain.model.Transaction
import com.finpulse.domain.model.TransactionType

fun TransactionWithCategoryRow.toDomain(): Transaction = Transaction(
    id = transaction.id,
    accountId = transaction.accountId,
    categoryId = transaction.categoryId,
    categoryName = categoryName,
    type = TransactionType.fromStorage(transaction.type),
    amountMinor = transaction.amountMinor,
    note = transaction.note,
    occurredAt = transaction.occurredAt,
    isRecurring = transaction.isRecurring,
    recurrenceRule = transaction.recurrenceRule,
)

fun TransactionEntity.toDomain(categoryName: String? = null): Transaction = Transaction(
    id = id,
    accountId = accountId,
    categoryId = categoryId,
    categoryName = categoryName,
    type = TransactionType.fromStorage(type),
    amountMinor = amountMinor,
    note = note,
    occurredAt = occurredAt,
    isRecurring = isRecurring,
    recurrenceRule = recurrenceRule,
)

fun GoalEntity.toDomain(): Goal = Goal(
    id = id,
    name = name,
    targetMinor = targetMinor,
    savedMinor = savedMinor,
    deadlineAt = deadlineAt,
    type = type,
)

fun DebtEntity.toDomain(): Debt = Debt(
    id = id,
    name = name,
    principalMinor = principalMinor,
    remainingMinor = remainingMinor,
    interestRate = interestRate,
    minimumPaymentMinor = minimumPaymentMinor,
    dueAt = dueAt,
    createdAt = createdAt,
)

fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    type = TransactionType.fromStorage(type),
    icon = icon,
    colorHex = colorHex,
)

fun Transaction.toEntity(userId: Long): TransactionEntity = TransactionEntity(
    id = id,
    userId = userId,
    accountId = accountId,
    categoryId = categoryId,
    type = type.storageValue,
    amountMinor = amountMinor,
    note = note,
    occurredAt = occurredAt,
    createdAt = System.currentTimeMillis(),
    isRecurring = isRecurring,
    recurrenceRule = recurrenceRule,
)

fun Debt.toEntity(userId: Long): DebtEntity = DebtEntity(
    id = id,
    userId = userId,
    name = name,
    principalMinor = principalMinor,
    remainingMinor = remainingMinor,
    interestRate = interestRate,
    minimumPaymentMinor = minimumPaymentMinor,
    dueAt = dueAt,
    createdAt = createdAt,
)
