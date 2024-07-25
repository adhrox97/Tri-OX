package com.adhrox.tri_xo.domain.model

import com.adhrox.tri_xo.data.network.model.GameData
import com.adhrox.tri_xo.data.network.model.PlayerData
import java.util.Calendar

data class GameModel(
    val board: List<PlayerType>,
    val player1: PlayerModel,
    val player2: PlayerModel?,
    val playerTurn: PlayerModel,
    val gameId: String,
    val isGameReady: Boolean = false,
    val isMyTurn: Boolean = false
)

fun GameData.toModel() = GameModel(
    board = board?.map { PlayerType.getPlayerById(it) } ?: mutableListOf(),
    gameId = gameId.orEmpty(),
    player1 = player1!!.toModel(),
    player2 = player2?.toModel(),
    playerTurn = playerTurn!!.toModel()
)

data class PlayerModel(
    val userName: String? = null,//Calendar.getInstance().timeInMillis.hashCode().toString(),
    val playerType: PlayerType
)

fun PlayerData.toModel() = PlayerModel(
    userName = userName,
    playerType = PlayerType.getPlayerById(playerType)
)

sealed class PlayerType(val id: Int, val symbol: String){
    data object FirstPlayer: PlayerType(2, "X")
    data object SecondPlayer: PlayerType(3, "O")
    data object Empty: PlayerType(0, "")

    companion object{
        fun getPlayerById(id: Int?): PlayerType{
            return when(id){
                FirstPlayer.id -> FirstPlayer
                SecondPlayer.id -> SecondPlayer
                else -> Empty
            }
        }
    }
}