package com.adhrox.tri_xo.ui.game

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adhrox.tri_xo.data.network.model.toData
import com.adhrox.tri_xo.domain.Repository
import com.adhrox.tri_xo.domain.model.GameModel
import com.adhrox.tri_xo.domain.model.GameStatus
import com.adhrox.tri_xo.domain.model.PlayerModel
import com.adhrox.tri_xo.domain.model.PlayerType
import com.adhrox.tri_xo.domain.model.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    companion object {
        private const val STAT_WIN = "win"
        private const val STAT_TIE = "tie"
        private const val STAT_LOSE = "lose"
        private const val STAT_TOTAL = "total"
    }

    //private lateinit var userName: String
    private lateinit var user: UserModel

    private val _game = MutableStateFlow<GameModel?>(null)
    val game: StateFlow<GameModel?> = _game

    private val _gameStatus = MutableStateFlow<GameStatus>(GameStatus.Ongoing())
    val gameStatus: StateFlow<GameStatus> = _gameStatus

    fun joinToGame(gameId: String, userName: String, owner: Boolean) {
        viewModelScope.launch {
            //this@GameViewModel.userName = userName
            this@GameViewModel.user =
                withContext(Dispatchers.IO) { repository.getCurrentUserModel() }
            if (owner) {
                join(gameId)
            } else {
                joinGameLikeGuest(gameId)
            }
        }
    }

    private fun join(gameId: String) {
        viewModelScope.launch {
            repository.joinToGame(gameId).collect {
                val result =
                    it?.copy(isGameReady = it.player2 != null, isMyTurn = isMyTurn(it.playerTurn))
                _game.value = result
                verifyWinner()
                restartGame()
            }
        }
    }

    private fun joinGameLikeGuest(gameId: String) {
        viewModelScope.launch {
            repository.joinToGame(gameId).take(1).collect {
                var result = it
                if (result != null) {
                    result = result.copy(
                        player2 = PlayerModel(
                            user.userName,
                            PlayerType.SecondPlayer,
                            false
                        )
                    )
                    repository.updateGame(result.toData())
                }
            }
            join(gameId)
        }
    }

    private fun isMyTurn(playerTurn: PlayerModel): Boolean {
        return playerTurn.userName == user.userName
    }

    fun onItemSelected(position: Int) {
        val currentGame = _game.value ?: return
        if (currentGame.isGameReady && currentGame.board[position] == PlayerType.Empty && isMyTurn(
                currentGame.playerTurn
            )
        ) {
            viewModelScope.launch {
                val newBoard = currentGame.board.toMutableList()
                newBoard[position] = getPlayer() ?: PlayerType.Empty
                repository.updateGame(
                    currentGame.copy(
                        board = newBoard,
                        playerTurn = getEnemyPlayer()!!
                    ).toData()
                )
            }
        }
    }

    private fun verifyWinner() {
        val board = _game.value?.board
        if (board != null && board.size == 9) {
            _gameStatus.value = isGameWon(board)
            updatePlayerStats(_gameStatus.value)
        }
    }

    private fun updatePlayerStats(gameStatus: GameStatus) {
        fun incrementStat(keyStat: String) {
            user.gamesInfo[keyStat] = user.gamesInfo[keyStat]!! + 1
        }

        when (gameStatus) {
            is GameStatus.Tie -> {
                incrementStat(STAT_TIE)
                incrementStat(STAT_TOTAL)
                repository.updateStatsUser(user.userName, user.gamesInfo)
            }

            is GameStatus.Won -> {
                if (gameStatus.player == user.userName) {
                    incrementStat(STAT_WIN)
                } else {
                    incrementStat(STAT_LOSE)
                }
                incrementStat(STAT_TOTAL)
                repository.updateStatsUser(user.userName, user.gamesInfo)
            }

            is GameStatus.Ongoing -> {}
        }
    }

    private fun isGameWon(board: List<PlayerType>): GameStatus {
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

        return when {
            checkWin(PlayerType.FirstPlayer) -> GameStatus.Won(_game.value?.player1?.userName)
            checkWin(PlayerType.SecondPlayer) -> GameStatus.Won(_game.value?.player2?.userName)
            board.all { it != PlayerType.Empty } -> GameStatus.Tie()
            else -> GameStatus.Ongoing()
        }
    }

    fun changeTryAgainStatus(owner: Boolean) {
        _game.value?.let { game ->
            val player = if (owner) game.player1 else game.player2

            player?.let {
                val updatedPlayer = it.copy(tryAgain = !it.tryAgain).toData()
                repository.updateTryAgain(game.gameId, updatedPlayer)
            }
            Log.i("adhrox", "Quiere reiniciar? player1: ${game.player1.tryAgain}, player2: ${game.player2?.tryAgain ?: "null"}")
        }
    }

    private fun restartGame() {
        val currentGame = _game.value ?: return
        if (currentGame.canTryAgain()) {
            val restartedBoard = currentGame.board.map { PlayerType.Empty }.toMutableList()
            repository.updateGame(
                currentGame.copy(
                    board = restartedBoard,
                    player1 = currentGame.player1.copy(tryAgain = false),
                    player2 = currentGame.player2?.copy(tryAgain = false)
                ).toData()
            )
            _gameStatus.value = GameStatus.Ongoing()
        }
    }

    /*private fun canTryAgain(game: GameModel): Boolean{
        return if (game.player2 != null){
            game.player1.tryAgain && game.player1.tryAgain
        } else {
            false
        }
    }*/

    private fun getPlayer(): PlayerType? {
        return when {
            game.value?.player1?.userName == user.userName -> PlayerType.FirstPlayer
            game.value?.player2?.userName == user.userName -> PlayerType.SecondPlayer
            else -> null
        }
    }

    private fun getEnemyPlayer(): PlayerModel? {
        return if (game.value?.player1?.userName == user.userName) game.value?.player2 else game.value?.player1
    }
}