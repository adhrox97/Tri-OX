package com.adhrox.tri_xo.domain.model

/*
sealed class GameStatus {
    data class Won(val player: PlayerType, val status: String = "ganador $player") : GameStatus()
    data class Tie(val status: String = "Empate") : GameStatus()
    data object Ongoing : GameStatus()
}*/

sealed class GameStatus(val player: String? = null, val status: String = "") {
    class Won(player: String?, status: String = "Ganador $player"): GameStatus(player, status)
    class Tie(status: String = "Empate") : GameStatus(status = status)
    class Ongoing(status: String = "En progreso") : GameStatus(status = status)

    fun toEnum(): GameStatusEnum{
        return when (this){
            is Won -> GameStatusEnum.WON
            is Tie -> GameStatusEnum.TIE
            is Ongoing -> GameStatusEnum.ONGOING
        }
    }

    companion object {
        fun fromEnum(enum: GameStatusEnum, player: String? = null): GameStatus {
            return when (enum) {
                GameStatusEnum.WON -> Won(player)
                GameStatusEnum.TIE -> Tie()
                GameStatusEnum.ONGOING -> Ongoing()
            }
        }
    }
}
