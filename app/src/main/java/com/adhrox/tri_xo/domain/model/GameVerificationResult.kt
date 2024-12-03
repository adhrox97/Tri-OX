package com.adhrox.tri_xo.domain.model

sealed class GameVerificationResult(val statusString: String) {
    data object GameFound : GameVerificationResult("Partida encontrada")
    data object GameFull : GameVerificationResult("Pardita llena")
    data object GameNotFound : GameVerificationResult("Partida no encontrada")
    data object GameFinished : GameVerificationResult("Partida finalizada")
}