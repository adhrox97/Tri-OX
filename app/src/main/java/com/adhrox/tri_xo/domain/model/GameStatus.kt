package com.adhrox.tri_xo.domain.model

import androidx.compose.ui.res.stringResource
import com.adhrox.tri_xo.R

sealed class GameStatus(val player: String? = null, val status: Int? = null) {
    class Won(player: String?, status: Int = R.string.winner_status): GameStatus(player, status)
    class Tie(status: Int = R.string.tie_game_status) : GameStatus(status = status)
    class Ongoing(status: Int = R.string.in_progress_game_status) : GameStatus(status = status)
    class Finished(status: Int = R.string.game_finished_status): GameStatus(status = status)

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
