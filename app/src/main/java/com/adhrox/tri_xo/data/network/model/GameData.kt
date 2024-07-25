package com.adhrox.tri_xo.data.network.model

import com.adhrox.tri_xo.domain.model.GameModel
import com.adhrox.tri_xo.domain.model.PlayerModel
import java.util.Calendar

data class GameData(
    val board: List<Int?>? = null,
    val gameId: String? = null,
    val player1: PlayerData? = null,
    val player2: PlayerData? = null,
    val playerTurn: PlayerData? = null
)

fun GameModel.toData() = GameData(
    board = board.map { it.id },
    gameId = gameId,
    player1 = player1.toData(),
    player2 = player2?.toData(),
    playerTurn = playerTurn.toData()
)

data class PlayerData(
    val userName: String? = null,//Calendar.getInstance().timeInMillis.hashCode().toString(),
    val playerType: Int? = null
)

fun PlayerModel.toData() = PlayerData(
    userName = userName,
    playerType = playerType.id
)