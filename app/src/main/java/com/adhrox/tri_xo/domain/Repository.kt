package com.adhrox.tri_xo.domain

import com.adhrox.tri_xo.data.network.model.GameData
import com.adhrox.tri_xo.domain.model.GameModel
import kotlinx.coroutines.flow.Flow

interface Repository {

    fun createGame(gameData: GameData): String

    fun joinToGame(gameId: String): Flow<GameModel?>

    fun updateGame(gameData: GameData)
}