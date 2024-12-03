package com.adhrox.tri_xo.data.network

import android.content.ContentValues.TAG
import android.util.Log
import com.adhrox.tri_xo.data.dto.UserDto
import com.adhrox.tri_xo.data.network.model.GameData
import com.adhrox.tri_xo.data.network.model.PlayerData
import com.adhrox.tri_xo.data.network.model.UserData
import com.adhrox.tri_xo.domain.AuthService
import com.adhrox.tri_xo.domain.Repository
import com.adhrox.tri_xo.domain.exceptions.UserAlreadyExistsException
import com.adhrox.tri_xo.domain.model.GameModel
import com.adhrox.tri_xo.domain.model.GameStatusEnum
import com.adhrox.tri_xo.domain.model.GameVerificationResult
import com.adhrox.tri_xo.domain.model.GameVerificationResult.GameFound
import com.adhrox.tri_xo.domain.model.GameVerificationResult.GameFull
import com.adhrox.tri_xo.domain.model.GameVerificationResult.GameNotFound
import com.adhrox.tri_xo.domain.model.GameVerificationResult.GameFinished
import com.adhrox.tri_xo.domain.model.UserModel
import com.adhrox.tri_xo.domain.model.toModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Provider
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class RepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val authServiceProvider: Provider<AuthService>
) : Repository {

    companion object {
        private const val PATH_GAMES = "games"
        private const val PATH_USERS = "users"
        private const val FIELD_GAMES_INFO = "gamesInfo"
        private const val FIELD_PLAYER1 = "player1"
        private const val FIELD_PLAYER2 = "player2"
        private const val FIELD_STATUS = "status"
    }

    private val authService: AuthService by lazy { authServiceProvider.get() }

    override fun createGame(gameData: GameData): String {
        val customId = getCustomId()
        val newGame = gameData.copy(gameId = customId)

        getDocumentReference(PATH_GAMES, customId).set(newGame)

        return customId
    }

    override fun joinToGame(gameId: String): Flow<GameModel?> {
        return getDocumentReference(PATH_GAMES, gameId)
            .snapshots()
            .map { ds ->
                ds.toObject(GameData::class.java)?.toModel()
            }
    }

    override suspend fun verifyGame(gameId: String): GameVerificationResult {

        val documentSnapshot = getDocumentReference(PATH_GAMES, gameId)
            .get()
            .await()

        return when {
            !documentSnapshot.exists() -> GameNotFound
            documentSnapshot.get("status") == "FINISHED" -> GameFinished
            documentSnapshot.get("player2") == null -> GameFound
            else -> GameFull
        }
    }

    override fun updateGame(gameData: GameData) {
        if (gameData.gameId != null) {
            getDocumentReference(PATH_GAMES, gameData.gameId).set(gameData)
        }
    }

    override fun updateTryAgain(gameId: String, player: PlayerData?) {
        player?.let {
            val field = if (it.playerType == 2) FIELD_PLAYER1 else FIELD_PLAYER2
            getDocumentReference(PATH_GAMES, gameId).update(field, it)
        }
    }

    override fun updateGameStatus(gameId: String, status: String) {
        getDocumentReference(PATH_GAMES, gameId).update(FIELD_STATUS, status)
    }

    override fun checkAndCreateUser(
        user: UserDto,
        cancellableContinuation: CancellableContinuation<Boolean>
    ) {
        val userName = user.userName

        getDocumentReference(PATH_USERS, userName)
            .get()
            .addOnSuccessListener {
                if (!it.exists()) {
                    createUser(user, cancellableContinuation)
                } else {
                    cancellableContinuation.resumeWithException(UserAlreadyExistsException())
                }
            }
            .addOnFailureListener { checkError ->
                cancellableContinuation.resumeWithException(checkError)
            }
    }

    override suspend fun getCurrentUserModel(): UserModel {
        val currentUser = authService.getCurrentUser()
        val query = db.collection(PATH_USERS).whereEqualTo("uid", currentUser!!.uid)
        return suspendCancellableCoroutine { cancellableContinuation ->
            query.get()
                .addOnSuccessListener {
                    val userModel = it.first().toObject(UserData::class.java).toModel()
                    cancellableContinuation.resume(userModel)
                }
                .addOnFailureListener {
                    cancellableContinuation.resumeWithException(it)
                }
        }
    }

    override fun updateStatsUser(userName: String, gameInfo: Map<String, Int>) {
        getDocumentReference(PATH_USERS, userName)
            .update(FIELD_GAMES_INFO, gameInfo)
            .addOnSuccessListener { Log.d(TAG, "Documento actualizado") }
            .addOnFailureListener { e -> Log.w(TAG, "Error al actualizar documento", e) }
    }

    private fun createUser(
        user: UserDto,
        cancellableContinuation: CancellableContinuation<Boolean>
    ) {
        getDocumentReference(PATH_USERS, user.userName).set(user)
            .addOnSuccessListener {
                cancellableContinuation.resume(true)
            }
            .addOnFailureListener { createError ->
                cancellableContinuation.resumeWithException(createError)
            }
    }

    private fun getDocumentReference(collectionPath: String, documentId: String): DocumentReference{
        return db.collection(collectionPath).document(documentId)
    }

    private fun getCustomId(): String {
        return Date().time.toString()
    }
}