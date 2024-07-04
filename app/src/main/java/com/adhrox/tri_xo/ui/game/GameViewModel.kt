package com.adhrox.tri_xo.ui.game

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adhrox.tri_xo.data.network.model.toData
import com.adhrox.tri_xo.domain.Repository
import com.adhrox.tri_xo.domain.model.GameModel
import com.adhrox.tri_xo.domain.model.PlayerModel
import com.adhrox.tri_xo.domain.model.PlayerType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(private val repository: Repository): ViewModel() {

    private lateinit var userId: String

    private val _game = MutableStateFlow<GameModel?>(null)
    val game: StateFlow<GameModel?> = _game

    private val _winner = MutableStateFlow<GameStatus>(GameStatus.Ongoing())
    val winner: StateFlow<GameStatus> = _winner

    fun joinToGame(gameId: String, userId: String, owner: Boolean) {
        this.userId = userId
        if (owner){
            join(gameId)
        }else{
            joinGameLikeGuest(gameId)
        }
    }

    private fun join(gameId: String) {
        viewModelScope.launch {
            repository.joinToGame(gameId).collect{
                val result = it?.copy(isGameReady = it.player2 != null, isMyTurn = isMyTurn(it.playerTurn))
                _game.value = result
                verifyWinner()
            }
        }
    }

    private fun joinGameLikeGuest(gameId: String) {
        viewModelScope.launch {
            repository.joinToGame(gameId).take(1).collect{
                var result = it
                if (result != null){
                    result = result.copy(player2 = PlayerModel(userId, PlayerType.SecondPlayer))
                    repository.updateGame(result.toData())
                }
            }
            join(gameId)
        }
    }

    private fun isMyTurn(playerTurn: PlayerModel): Boolean{
        return playerTurn.userId == userId
    }

    fun onItemSelected(position: Int) {
        val currentGame = _game.value ?: return
        if (currentGame.isGameReady && currentGame.board[position] == PlayerType.Empty && isMyTurn(currentGame.playerTurn)){
            viewModelScope.launch {
                val newBoard = currentGame.board.toMutableList()
                newBoard[position] = getPlayer() ?: PlayerType.Empty
                repository.updateGame(currentGame.copy(board = newBoard, playerTurn = getEnemyPlayer()!!).toData())
            }
        }
    }

    private fun verifyWinner(){
        val board = _game.value?.board
        if (board != null && board.size == 9){
            _winner.value = isGameWon(board)
        }
    }

    private fun isGameWon(board: List<PlayerType>): GameStatus{
        fun checkWin(playerType: PlayerType): Boolean {
            return when {
                //Row
                (board[0] == playerType && board[1] == playerType && board[2] == playerType) -> true
                (board[3] == playerType && board[4] == playerType && board[5] == playerType) -> true
                (board[6] == playerType && board[7] == playerType && board[8] == playerType) -> true
                //Column
                (board[0] == playerType && board[3] == playerType && board[6] == playerType) -> true
                (board[1] == playerType && board[4] == playerType && board[7] == playerType) -> true
                (board[2] == playerType && board[5] == playerType && board[8] == playerType) -> true
                //Diagonal
                (board[0] == playerType && board[4] == playerType && board[8] == playerType) -> true
                (board[2] == playerType && board[4] == playerType && board[6] == playerType) -> true
                else -> false
            }
        }

        return when{
            checkWin(PlayerType.FirstPlayer) -> GameStatus.Won(PlayerType.FirstPlayer)
            checkWin(PlayerType.SecondPlayer) -> GameStatus.Won(PlayerType.SecondPlayer)
            board.all{ it != PlayerType.Empty } -> GameStatus.Tie()
            else -> GameStatus.Ongoing()
        }
    }

    fun restartGame(){
        val currentGame = _game.value ?: return
        val restartedBoard = currentGame.board.map { PlayerType.Empty }.toMutableList()
        repository.updateGame(currentGame.copy(board = restartedBoard).toData())
        _winner.value = GameStatus.Ongoing()
    }

    private fun getPlayer(): PlayerType?{
        return when{
            game.value?.player1?.userId == userId -> PlayerType.FirstPlayer
            game.value?.player2?.userId == userId -> PlayerType.SecondPlayer
            else -> null
        }
    }

    private fun getEnemyPlayer(): PlayerModel?{
        return if (game.value?.player1?.userId == userId) game.value?.player2 else game.value?.player1
    }
}

sealed class GameStatus(val player: PlayerType? = null, val status: String = "") {
    class Won(player: PlayerType?, status: String = "Ganador $player"): GameStatus(player, status)
    class Tie(status: String = "Empate") : GameStatus(status = status)
    class Ongoing(status: String = "En progreso") : GameStatus(status = status)
}

/*
sealed class GameStatus {
    data class Won(val player: PlayerType, val status: String = "ganador $player") : GameStatus()
    data class Tie(val status: String = "Empate") : GameStatus()
    data object Ongoing : GameStatus()
}*/
