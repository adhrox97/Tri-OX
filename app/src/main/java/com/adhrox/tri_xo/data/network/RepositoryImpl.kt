package com.adhrox.tri_xo.data.network

import com.adhrox.tri_xo.data.network.model.GameData
import com.adhrox.tri_xo.domain.Repository
import com.adhrox.tri_xo.domain.model.GameModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore): Repository {

    companion object{
        private const val PATH = "games"
    }
    override fun createGame(gameData: GameData): String{
        return ""
    }

    override fun joinToGame(gameId: String): Flow<GameModel?> {

    }

    override fun updateGame(gameData: GameData) {

    }

}