package com.adhrox.tri_xo.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adhrox.tri_xo.data.network.model.GameData
import com.adhrox.tri_xo.data.network.model.PlayerData
import com.adhrox.tri_xo.domain.Repository
import com.google.protobuf.Internal.BooleanList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: Repository): ViewModel() {

    private val _gameState = MutableStateFlow<GameState>(GameState())
    val gameState: StateFlow<GameState> = _gameState

    fun onCreateGame(navigateToGame: (String, String, Boolean) -> Unit) {
        val game = createNewGame()
        val gameId = repository.createGame(game)
        val userId = game.player1?.userId.orEmpty()
        val owner = true
        _gameState.value = GameState()
        navigateToGame(gameId, userId, owner)
    }

    fun onJoinGame(gameId: String, navigateToGame: (String, String, Boolean) -> Unit) {
        val owner = false
        _gameState.value = GameState()
        navigateToGame(gameId, createUserId(), owner)
    }

    fun verifyGame(gameId: String){
        viewModelScope.launch {
            val result = async { repository.verifyGame(gameId) }.await()
            if (result){
                _gameState.update { it.copy(gameId = gameId, found = true) }
            }else{
                _gameState.value = GameState()
                delay(50)
                _gameState.update { it.copy(found = false) }
            }
        }
    }

    private fun createUserId(): String{
        return Calendar.getInstance().timeInMillis.hashCode().toString()
    }

    private fun createNewGame(): GameData {
        val currentPlayer = PlayerData(playerType = 2)

        return GameData(
            board = List(9) { 0 },
            player1 = currentPlayer,
            player2 = null,
            playerTurn = currentPlayer
        )
    }
}

data class GameState(
    val gameId: String = "",
    val found: Boolean? = null
)