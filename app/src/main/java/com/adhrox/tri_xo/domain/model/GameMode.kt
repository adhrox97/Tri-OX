package com.adhrox.tri_xo.domain.model

import com.adhrox.tri_xo.domain.model.GameStatus.Finished
import com.adhrox.tri_xo.domain.model.GameStatus.Ongoing
import com.adhrox.tri_xo.domain.model.GameStatus.Tie
import com.adhrox.tri_xo.domain.model.GameStatus.Won

sealed class GameMode(val mode: GameModeEnum) {
    data object Standard : GameMode(GameModeEnum.STANDARD)
    data object Limited : GameMode(GameModeEnum.LIMITED)

    companion object {
        fun fromEnum(enumValue: String): GameMode {

            val enum = GameModeEnum.valueOf(enumValue)

            return when (enum) {
                GameModeEnum.STANDARD -> Standard
                GameModeEnum.LIMITED -> Limited
            }
        }
    }
}