package com.adhrox.tri_xo.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adhrox.tri_xo.data.network.model.BoardCellData
import com.adhrox.tri_xo.data.network.model.GameData
import com.adhrox.tri_xo.data.network.model.PlayerData
import com.adhrox.tri_xo.domain.Repository
import com.adhrox.tri_xo.domain.model.GameStatusEnum
import com.adhrox.tri_xo.domain.model.GameVerificationResult
import com.adhrox.tri_xo.domain.model.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: Repository): ViewModel() {

    private val _gameState = MutableStateFlow<GameState>(GameState())
    val gameState: StateFlow<GameState> = _gameState

    fun getCurrentUserModel(){
        viewModelScope.launch {
            showLoading(true)
            val user = withContext(Dispatchers.IO) { repository.getCurrentUserModel() }
            _gameState.update { it.copy(user = user) }
            showLoading(false)
        }
    }

    fun onCreateGame(gameMode: String, navigateToGame: (String, String, Boolean) -> Unit) {
        val game = createNewGame(gameMode)
        val gameId = repository.createGame(game)
        val userName = game.player1?.userName.orEmpty()
        val owner = true
        _gameState.value = GameState()
        navigateToGame(gameId, userName, owner)
    }

    fun onJoinGame(gameId: String, navigateToGame: (String, String, Boolean) -> Unit) {
        viewModelScope.launch {
            verifyGame(gameId)
            if (_gameState.value.gameVerification == GameVerificationResult.GameFound){
                val owner = false
                navigateToGame(gameId, _gameState.value.user.userName, owner)
            }
        }
    }

    private suspend fun verifyGame(gameId: String){
        val result = repository.verifyGame(gameId)
        _gameState.update { it.copy(gameVerification = result) }
    }

    private fun createNewGame(gameMode: String): GameData {
        val currentPlayer = PlayerData(userName = _gameState.value.user.userName, playerType = 2, tryAgain = false)

        return GameData(
            board = List(9) { BoardCellData(0, 0L) },
            player1 = currentPlayer,
            player2 = null,
            playerTurn = currentPlayer,
            gameMode = gameMode,
            status = GameStatusEnum.ONGOING.value
        )
    }

    fun resetGameVerificationState(){
        _gameState.update { it.copy(gameVerification = null) }
    }

    private fun showLoading(state: Boolean){
        _gameState.update { it.copy(isLoading = state) }
    }
}

data class GameState(
    val gameVerification: GameVerificationResult? = null,
    val user: UserModel = UserModel("", "", "", mutableMapOf()),
    val isLoading: Boolean = false
)