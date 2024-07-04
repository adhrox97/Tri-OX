package com.adhrox.tri_xo.data.network

import android.util.Log
import com.adhrox.tri_xo.data.network.model.GameData
import com.adhrox.tri_xo.domain.Repository
import com.adhrox.tri_xo.domain.model.GameModel
import com.adhrox.tri_xo.domain.model.toModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.withIndex
import java.util.Date
import javax.inject.Inject

class RepositoryImpl @Inject constructor(private val db: FirebaseFirestore): Repository {

    companion object{
        private const val PATH_GAMES = "games"
    }
    override fun createGame(gameData: GameData): String{
        val customId = getCustomId()
        val newGame = gameData.copy(gameId = customId)
        db.collection(PATH_GAMES).document(customId).set(newGame)
        return customId
    }

    override fun joinToGame(gameId: String): Flow<GameModel?> {
        return db
            .collection(PATH_GAMES)
            .document(gameId)
            .snapshots()
            .map { ds ->
                ds.toObject(GameData::class.java)?.toModel()
            }
    }

    override suspend fun verifyGame(gameId: String): Boolean {
        return db
            .collection(PATH_GAMES)
            .document(gameId)
            .snapshots().first().exists()
    }

    override fun updateGame(gameData: GameData) {
        if (gameData.gameId != null){
            db.collection(PATH_GAMES).document(gameData.gameId).set(gameData)
        }
    }

    private fun getCustomId(): String{
        return Date().time.toString()
    }

}