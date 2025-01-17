package com.adhrox.tri_xo.ui.game

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.adhrox.tri_xo.data.network.model.BoardCellData
import com.adhrox.tri_xo.data.network.model.toData
import com.adhrox.tri_xo.data.network.workers.CancelGameWorker
import com.adhrox.tri_xo.domain.Repository
import com.adhrox.tri_xo.domain.model.BoardCellModel
import com.adhrox.tri_xo.domain.model.GameMode
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
class GameViewModel @Inject constructor(
    private val repository: Repository,
    private val workManager: WorkManager
) : ViewModel() {

    companion object {
        private const val STAT_WIN = "win"
        private const val STAT_TIE = "tie"
        private const val STAT_LOSE = "lose"
        private const val STAT_TOTAL = "total"
    }

    private lateinit var user: UserModel

    private val _game = MutableStateFlow<GameModel?>(null)
    val game: StateFlow<GameModel?> = _game

    override fun onCleared() {
        super.onCleared()
        scheduleCancelGameWorker()
    }

    fun joinToGame(gameId: String, owner: Boolean) {
        viewModelScope.launch {
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
                val isBoardChange = it?.board != _game.value?.board
                val result =
                    it?.copy(isGameReady = it.player2 != null, isMyTurn = isMyTurn(it.playerTurn))
                _game.value = result
                if (isBoardChange) {
                    verifyWinner()
                }
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
        if (currentGame.isGameReady && currentGame.board[position].player == PlayerType.Empty && isMyTurn(
                currentGame.playerTurn
            )
        ) {
            viewModelScope.launch {
                var newBoard = currentGame.board.toMutableList()
                val player = currentGame.playerTurn.playerType
                newBoard[position] = newBoard[position].copy(
                    player = getPlayer() ?: PlayerType.Empty,
                    timeStamp = System.currentTimeMillis()
                )

                if (currentGame.gameMode is GameMode.Limited) {
                    newBoard = removeOldestMove(newBoard, player)
                }
                repository.updateGame(
                    currentGame.copy(
                        board = newBoard,
                        playerTurn = getEnemyPlayer()!!
                    ).toData()
                )
            }
        }
    }

    private fun removeOldestMove(
        board: MutableList<BoardCellModel>,
        playerType: PlayerType
    ): MutableList<BoardCellModel> {
        val playerMoves = board
            .mapIndexed { index, boardCell -> index to boardCell }
            .filter { it.second.player.id == playerType.id }
            .sortedBy { it.second.timeStamp }

        if (playerMoves.size > 3) {
            val (oldestIndex, _) = playerMoves.first()
            board[oldestIndex] = BoardCellModel(PlayerType.Empty, 0L)
        }

        return board
    }

    private fun verifyWinner() {
        val board = _game.value?.board
        if (board != null && board.size == 9) {
            val status = isGameWon(board)
            updateGamesStatus(status)
            updatePlayerStats(status)
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

            is GameStatus.Finished -> {}
        }
    }

    private fun isGameWon(board: List<BoardCellModel>): GameStatus {
        fun checkWin(playerType: PlayerType): Boolean {
            return when {
                //Row
                (board[0].player == playerType && board[1].player == playerType && board[2].player == playerType) -> true
                (board[3].player == playerType && board[4].player == playerType && board[5].player == playerType) -> true
                (board[6].player == playerType && board[7].player == playerType && board[8].player == playerType) -> true
                //Column
                (board[0].player == playerType && board[3].player == playerType && board[6].player == playerType) -> true
                (board[1].player == playerType && board[4].player == playerType && board[7].player == playerType) -> true
                (board[2].player == playerType && board[5].player == playerType && board[8].player == playerType) -> true
                //Diagonal
                (board[0].player == playerType && board[4].player == playerType && board[8].player == playerType) -> true
                (board[2].player == playerType && board[4].player == playerType && board[6].player == playerType) -> true
                else -> false
            }
        }

        return when {
            checkWin(PlayerType.FirstPlayer) -> GameStatus.Won(_game.value?.player1?.userName)
            checkWin(PlayerType.SecondPlayer) -> GameStatus.Won(_game.value?.player2?.userName)
            board.all { it.player != PlayerType.Empty } -> GameStatus.Tie()
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
        }
    }

    private fun restartGame() {
        val currentGame = _game.value ?: return
        if (currentGame.canTryAgain()) {
            val restartedBoard = List(9) { BoardCellModel(PlayerType.Empty, 0L) }.toMutableList()
            repository.updateGame(
                currentGame.copy(
                    board = restartedBoard,
                    player1 = currentGame.player1.copy(tryAgain = false),
                    player2 = currentGame.player2?.copy(tryAgain = false)
                ).toData()
            )
            updateGamesStatus(GameStatus.Ongoing())
        }
    }

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

    private fun updateGamesStatus(gameStatus: GameStatus) {
        _game.value?.let {
            repository.updateGameStatus(it.gameId, gameStatus.toEnumValue())
        }
    }

    private fun scheduleCancelGameWorker() {
        _game.value?.let {
            val inputData = workDataOf(CancelGameWorker.KEY_GAME_ID to it.gameId)

            val cancelGameRequest = OneTimeWorkRequestBuilder<CancelGameWorker>()
                .setInputData(inputData)
                .build()

            workManager.enqueue(cancelGameRequest)
        }
    }
}