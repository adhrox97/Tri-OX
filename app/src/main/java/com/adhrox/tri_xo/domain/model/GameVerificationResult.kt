package com.adhrox.tri_xo.domain.model

sealed class GameVerificationResult {
    data object GameFound : GameVerificationResult()
    data object GameFull : GameVerificationResult()
    data object GameNotFound : GameVerificationResult()
}