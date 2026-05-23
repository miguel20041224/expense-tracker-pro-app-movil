package com.finpulse.data.session

import com.finpulse.data.local.prefs.AppPreferences
import kotlinx.coroutines.flow.first
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentUserProvider @Inject constructor(
    private val preferences: AppPreferences,
) {
    private val cachedUserId = AtomicLong(-1L)

    fun setUserId(userId: Long) {
        cachedUserId.set(userId)
    }

    fun clear() {
        cachedUserId.set(-1L)
    }

    fun requireUserId(): Long {
        val id = cachedUserId.get()
        check(id > 0L) { "No hay sesión activa" }
        return id
    }

    fun currentUserIdOrNull(): Long? = cachedUserId.get().takeIf { it > 0L }

    suspend fun restoreFromPreferences(): Long? {
        val id = preferences.loggedInUserId.first() ?: return null
        cachedUserId.set(id)
        return id
    }
}
