package com.adhrox.tri_xo.domain.model

import com.adhrox.tri_xo.R

sealed class GameVerificationResult(val refStatus: Int) {
    data object GameFound : GameVerificationResult(R.string.game_found_status)
    data object GameFull : GameVerificationResult(R.string.game_full_status)
    data object GameNotFound : GameVerificationResult(R.string.game_not_found_status)
    data object GameFinished : GameVerificationResult(R.string.game_finished_status)
}