package com.adhrox.tri_xo.domain

import com.adhrox.tri_xo.data.dto.UserDto
import com.adhrox.tri_xo.data.network.model.GameData
import com.adhrox.tri_xo.data.network.model.UserData
import com.adhrox.tri_xo.domain.model.GameModel
import com.adhrox.tri_xo.domain.model.UserModel
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun createGame(gameData: GameData): String
    fun joinToGame(gameId: String): Flow<GameModel?>
    fun updateGame(gameData: GameData)
    suspend fun verifyGame(gameId: String): Boolean
    fun createUser(user: UserDto, cancellableContinuation: CancellableContinuation<Boolean>)
    suspend fun getCurrentUserModel(): UserModel
    suspend fun isUserAlreadyExist(userName: String)
}