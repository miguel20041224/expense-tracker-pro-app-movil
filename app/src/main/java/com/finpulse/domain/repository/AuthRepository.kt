package com.finpulse.domain.repository

import com.finpulse.domain.model.User

interface AuthRepository {
    suspend fun register(username: String, password: String, displayName: String): Result<User>
    suspend fun login(username: String, password: String): Result<User>
    suspend fun logout()
    suspend fun currentUser(): User?
    fun observeLoggedInUserId(): kotlinx.coroutines.flow.Flow<Long?>
}
