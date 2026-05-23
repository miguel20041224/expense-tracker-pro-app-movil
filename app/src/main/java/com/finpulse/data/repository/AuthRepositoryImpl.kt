package com.finpulse.data.repository

import com.finpulse.data.local.db.dao.UserDao
import com.finpulse.data.local.db.entity.UserEntity
import com.finpulse.data.local.prefs.AppPreferences
import com.finpulse.data.local.seed.DemoDataSeeder
import com.finpulse.data.session.CurrentUserProvider
import com.finpulse.domain.model.User
import com.finpulse.domain.repository.AuthRepository
import com.finpulse.util.PasswordHasher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val preferences: AppPreferences,
    private val currentUser: CurrentUserProvider,
    private val demoDataSeeder: DemoDataSeeder,
) : AuthRepository {

    override suspend fun register(username: String, password: String, displayName: String): Result<User> {
        val normalized = username.trim().lowercase()
        if (normalized.length < 3) return Result.failure(IllegalArgumentException("Usuario muy corto"))
        if (password.length < 4) return Result.failure(IllegalArgumentException("Contraseña muy corta"))
        if (userDao.findByUsername(normalized) != null) {
            return Result.failure(IllegalArgumentException("Usuario ya existe"))
        }
        val now = System.currentTimeMillis()
        val id = userDao.insert(
            UserEntity(
                username = normalized,
                passwordHash = PasswordHasher.hash(password, normalized),
                displayName = displayName.ifBlank { normalized },
                createdAt = now,
            ),
        )
        return openSession(id, normalized, displayName)
    }

    override suspend fun login(username: String, password: String): Result<User> {
        val normalized = username.trim().lowercase()
        val entity = userDao.findByUsername(normalized)
            ?: return Result.failure(IllegalArgumentException("Usuario o contraseña incorrectos"))
        if (!PasswordHasher.verify(password, normalized, entity.passwordHash)) {
            return Result.failure(IllegalArgumentException("Usuario o contraseña incorrectos"))
        }
        return openSession(entity.id, entity.username, entity.displayName)
    }

    override suspend fun logout() {
        preferences.setLoggedInUserId(null)
        currentUser.clear()
    }

    override suspend fun currentUser(): User? {
        val id = currentUser.currentUserIdOrNull() ?: preferences.loggedInUserId.first() ?: return null
        val entity = userDao.findById(id) ?: return null
        return User(entity.id, entity.username, entity.displayName)
    }

    override fun observeLoggedInUserId(): Flow<Long?> = preferences.loggedInUserId

    private suspend fun openSession(id: Long, username: String, displayName: String): Result<User> {
        preferences.setLoggedInUserId(id)
        currentUser.setUserId(id)
        demoDataSeeder.ensureCatalog(id)
        if (!preferences.isDemoSeeded.first()) {
            demoDataSeeder.seedIfEmpty(id)
            preferences.markDemoSeeded()
        }
        return Result.success(User(id, username, displayName.ifBlank { username }))
    }
}
