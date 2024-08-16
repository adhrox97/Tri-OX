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
import com.adhrox.tri_xo.domain.model.GameVerificationResult
import com.adhrox.tri_xo.domain.model.GameVerificationResult.*
import com.adhrox.tri_xo.domain.model.UserModel
import com.adhrox.tri_xo.domain.model.toModel
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
    }

    private val authService: AuthService by lazy { authServiceProvider.get() }

    override fun createGame(gameData: GameData): String {
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

    override suspend fun verifyGame(gameId: String): GameVerificationResult {

        val documentSnapshot = db
            .collection(PATH_GAMES)
            .document(gameId)
            .get()
            .await()

        return when {
            !documentSnapshot.exists() -> GameNotFound
            documentSnapshot.get("player2") == null -> GameFound
            else -> GameFull
        }
    }

    override fun updateGame(gameData: GameData) {
        if (gameData.gameId != null) {
            db.collection(PATH_GAMES).document(gameData.gameId).set(gameData)
        }
    }

    override fun updateTryAgain(gameId: String, player: PlayerData?) {
        val reference = db.collection(PATH_GAMES).document(gameId)
        player?.let {
            val field = if (it.playerType == 2) FIELD_PLAYER1 else FIELD_PLAYER2
            reference.update(field, it)
        }
    }

    override fun createUser(
        user: UserDto,
        cancellableContinuation: CancellableContinuation<Boolean>
    ) {
        db.collection(PATH_USERS).document(user.userName).set(user)
            .addOnSuccessListener {
                cancellableContinuation.resume(true)
            }
            .addOnFailureListener {
                cancellableContinuation.resumeWithException(it)
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
        val userRef = db.collection(PATH_USERS).document(userName)

        userRef
            .update(FIELD_GAMES_INFO, gameInfo)
            .addOnSuccessListener { Log.d(TAG, "Documento actualizado") }
            .addOnFailureListener { e -> Log.w(TAG, "Error al actualizar documento", e) }
    }

    private fun getCustomId(): String {
        return Date().time.toString()
    }

    override suspend fun isUserAlreadyExist(
        userName: String
    ) {
        return suspendCancellableCoroutine { cancellableContinuation ->
            db.collection(PATH_USERS)
                .document(userName)
                .get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        cancellableContinuation.resumeWithException(UserAlreadyExistsException())
                    } else {
                        cancellableContinuation.resume(Unit)
                    }
                }
                .addOnFailureListener {
                    cancellableContinuation.resumeWithException(it)
                }
        }
    }
}