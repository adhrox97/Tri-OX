package com.adhrox.tri_xo.domain

import com.adhrox.tri_xo.data.dto.UserDto
import com.adhrox.tri_xo.data.network.model.GameData
import com.adhrox.tri_xo.data.network.model.PlayerData
import com.adhrox.tri_xo.data.network.model.UserData
import com.adhrox.tri_xo.domain.model.GameModel
import com.adhrox.tri_xo.domain.model.GameVerificationResult
import com.adhrox.tri_xo.domain.model.UserModel
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun createGame(gameData: GameData): String
    fun joinToGame(gameId: String): Flow<GameModel?>
    fun updateGame(gameData: GameData)
    fun updateTryAgain(gameId: String, player: PlayerData?)
    suspend fun verifyGame(gameId: String): GameVerificationResult
    fun createUser(user: UserDto, cancellableContinuation: CancellableContinuation<Boolean>)
    suspend fun getCurrentUserModel(): UserModel
    fun updateStatsUser(userName: String, gameInfo: Map<String, Int>)
    suspend fun isUserAlreadyExist(userName: String)
}