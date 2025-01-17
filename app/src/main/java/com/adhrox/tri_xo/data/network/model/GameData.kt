package com.adhrox.tri_xo.data.network.model

import com.adhrox.tri_xo.domain.model.BoardCellModel
import com.adhrox.tri_xo.domain.model.GameModel
import com.adhrox.tri_xo.domain.model.GameStatus
import com.adhrox.tri_xo.domain.model.PlayerModel
import java.util.Calendar

data class GameData(
    val board: List<BoardCellData?>? = null,
    val gameId: String? = null,
    val player1: PlayerData? = null,
    val player2: PlayerData? = null,
    val playerTurn: PlayerData? = null,
    val gameMode: String? = null,
    val status: String? = null
)

fun GameModel.toData() = GameData(
    board = board.map { it.toData() },
    gameId = gameId,
    player1 = player1.toData(),
    player2 = player2?.toData(),
    playerTurn = playerTurn.toData(),
    gameMode = gameMode.mode.name,
    status = status.toEnumValue()
)

data class BoardCellData(
    val player: Int? = null,
    val timeStamp: Long? = null
)

fun BoardCellModel.toData() = BoardCellData(
    player = player.id,
    timeStamp = timeStamp
)

data class PlayerData(
    val userName: String? = null,//Calendar.getInstance().timeInMillis.hashCode().toString(),
    val playerType: Int? = null,
    val tryAgain: Boolean? = null
)

fun PlayerModel.toData() = PlayerData(
    userName = userName,
    playerType = playerType.id,
    tryAgain = tryAgain
)