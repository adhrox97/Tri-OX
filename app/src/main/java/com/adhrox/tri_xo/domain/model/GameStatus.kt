package com.adhrox.tri_xo.domain.model

/*
sealed class GameStatus {
    data class Won(val player: PlayerType, val status: String = "ganador $player") : GameStatus()
    data class Tie(val status: String = "Empate") : GameStatus()
    data object Ongoing : GameStatus()
}*/

sealed class GameStatus(val player: String? = null, val status: String = "") {
    class Won(player: String?, status: String = "Ganador\n$player"): GameStatus(player, status)
    class Tie(status: String = "Empate") : GameStatus(status = status)
    class Ongoing(status: String = "En progreso") : GameStatus(status = status)
    class Finished(status: String = "Partida finalizada"): GameStatus(status = status)

    fun toEnumValue(): String{
        return when (this){
            is Won -> "${GameStatusEnum.WON.value}:${this.player}"
            is Tie -> GameStatusEnum.TIE.value
            is Ongoing -> GameStatusEnum.ONGOING.value
            is Finished -> GameStatusEnum.FINISHED.value
        }
    }

    companion object {
        fun fromEnum(enumValue: String): GameStatus {

            val enumParts = enumValue.split(":")
            val player = if (enumParts.size > 1) enumParts[1] else null
            val enum = GameStatusEnum.valueOf(enumParts[0])

            return when (enum) {
                GameStatusEnum.WON -> Won(player)
                GameStatusEnum.TIE -> Tie()
                GameStatusEnum.ONGOING -> Ongoing()
                GameStatusEnum.FINISHED -> Finished()
            }
        }
    }
}
